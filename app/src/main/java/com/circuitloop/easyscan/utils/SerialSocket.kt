package com.circuitloop.easyscan.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDeviceConnection
import com.circuitloop.easyscan.view.main.listener.SerialListener
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.io.IOException
import java.util.concurrent.Executors

class SerialSocket internal constructor() : SerialInputOutputManager.Listener {
    private val disconnectBroadcastReceiver: BroadcastReceiver
    private var context: Context? = null
    private var listener: SerialListener? = null
    private var connection: UsbDeviceConnection? = null
    private var serialPort: UsbSerialPort? = null
    private var ioManager: SerialInputOutputManager? = null
    @Throws(IOException::class)
    fun connect(
        context: Context,
        listener: SerialListener?,
        connection: UsbDeviceConnection?,
        serialPort: UsbSerialPort,
        baudRate: Int
    ) {
        if (this.serialPort != null) throw IOException("already connected")
        this.context = context
        this.listener = listener
        this.connection = connection
        this.serialPort = serialPort
        context.registerReceiver(
            disconnectBroadcastReceiver,
            IntentFilter(Constants.INTENT_ACTION_DISCONNECT)
        )
        serialPort.open(connection)
        serialPort.setParameters(
            baudRate,
            UsbSerialPort.DATABITS_8,
            UsbSerialPort.STOPBITS_1,
            UsbSerialPort.PARITY_NONE
        )
        serialPort.dtr = true // for arduino, ...
        serialPort.rts = true
        ioManager = SerialInputOutputManager(serialPort, this)
        Executors.newSingleThreadExecutor().submit(ioManager)
    }

    fun disconnect() {
        listener = null // ignore remaining data and errors
        if (ioManager != null) {
            ioManager!!.listener = null
            ioManager!!.stop()
            ioManager = null
        }
        if (serialPort != null) {
            try {
                serialPort!!.dtr = false
                serialPort!!.rts = false
            } catch (ignored: Exception) {
            }
            try {
                serialPort!!.close()
            } catch (ignored: Exception) {
            }
            serialPort = null
        }
        if (connection != null) {
            connection!!.close()
            connection = null
        }
        try {
            context!!.unregisterReceiver(disconnectBroadcastReceiver)
        } catch (ignored: Exception) {
        }
    }

    @Throws(IOException::class)
    fun write(data: ByteArray?) {
        if (serialPort == null) throw IOException("not connected")
        serialPort!!.write(data,
            WRITE_WAIT_MILLIS
        )
    }

    override fun onNewData(data: ByteArray) {
        listener?.onSerialRead(data)
    }

    override fun onRunError(e: Exception) {
        listener?.onSerialIoError(e)
    }

    companion object {
        private const val WRITE_WAIT_MILLIS = 2000 // 0 blocked infinitely on unprogrammed arduino
    }

    init {
        disconnectBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                listener?.onSerialIoError(IOException("background disconnect"))
                disconnect() // disconnect now, else would be queued until UI re-attached
            }
        }
    }
}
