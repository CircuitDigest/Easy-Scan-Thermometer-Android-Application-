package com.circuitloop.easyscan.view.main.fragment

import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.circuitloop.easyscan.R
import com.circuitloop.easyscan.app.MainApplication
import com.circuitloop.easyscan.database.DetailsTable
import com.circuitloop.easyscan.services.SerialService
import com.circuitloop.easyscan.utils.Constants
import com.circuitloop.easyscan.utils.Constants.CAMERA_INTENT
import com.circuitloop.easyscan.utils.Constants.INTENT_KEY_HOMEBASEMODEL
import com.circuitloop.easyscan.utils.Constants.INTENT_KEY_VIEWFINDERHEIGHT
import com.circuitloop.easyscan.utils.Constants.INTENT_KEY_VIEWFINDERWIDTH
import com.circuitloop.easyscan.utils.Constants.RESULT_INTENT
import com.circuitloop.easyscan.utils.CustomProber
import com.circuitloop.easyscan.utils.SerialSocket
import com.circuitloop.easyscan.utils.SharedPreferencesHolder
import com.circuitloop.easyscan.view.main.activity.CameraActivity
import com.circuitloop.easyscan.view.main.activity.MainActivity
import com.circuitloop.easyscan.view.main.listener.SerialListener
import com.circuitloop.easyscan.viewmodel.main.MainViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.android.synthetic.main.fragment_terminal.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class TerminalFragment : Fragment(),
    ServiceConnection, SerialListener {
    private enum class Connected {
        False, Pending, True
    }

    private var isSoundEnabled: Boolean = true

    private var mData: String = ""
    private var deviceName: String? = ""
    private var isSuspected: Boolean = false
    private var isDelayed: Boolean = false
    private var deviceId = 0
    private var portNum = 0
    private val baudRate = 9600
    private var newline = "\r\n"
    private var usbSerialPort: UsbSerialPort? = null
    private var socket: SerialSocket? = null
    private var service: SerialService? = null
    private var initialStart = true
    private var connected =
        Connected.False
    private val broadcastReceiver: BroadcastReceiver
    private var controlLines: ControlLines? = null
    val mViewModel: MainViewModel by viewModel()
    var mDataCounter = 0
    /*
     * Lifecycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        getActivity()?.getWindow()?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        arguments?.let {
            deviceId = it.getInt("device")
            portNum = it.getInt("port")
            deviceName = it.getString("devicename")
        }
    }

    override fun onDestroy() {
        if (connected != Connected.False) disconnect()
        activity!!.stopService(Intent(activity, SerialService::class.java))
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        if (service != null) service!!.attach(this) else activity!!.startService(
            Intent(
                activity,
                SerialService::class.java
            )
        ) // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    override fun onStop() {
        if (service != null && !activity!!.isChangingConfigurations) service!!.detach()
        super.onStop()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        getActivity()!!.bindService(
            Intent(getActivity(), SerialService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onDetach() {
        try {
            activity!!.unbindService(this)
        } catch (ignored: Exception) {
        }
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        activity!!.registerReceiver(
            broadcastReceiver,
            IntentFilter(Constants.INTENT_ACTION_GRANT_USB)
        )
        if (initialStart && service != null) {
            initialStart = false
            activity!!.runOnUiThread { connect() }
        }
        if (controlLines != null && connected == Connected.True) controlLines!!.start()
    }

    override fun onPause() {
        activity!!.unregisterReceiver(broadcastReceiver)
        if (controlLines != null) controlLines!!.stop()
        super.onPause()
    }

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        service = (binder as SerialService.SerialBinder).service

        if (initialStart && isResumed) {
            initialStart = false
            activity!!.runOnUiThread { connect() }
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        service = null
    }

    /*
     * UI
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_terminal, container, false)

       // receive_text?.setTextColor(resources.getColor(R.color.colorRecieveText)) // set as default color to reduce number of spans
       // receive_text?.setMovementMethod(ScrollingMovementMethod.getInstance())
        val sendText = view.findViewById<TextView>(R.id.send_text)
        val sendBtn = view.findViewById<View>(R.id.send_btn)
        val deviceIdTxt = view.findViewById<TextView>(R.id.device_id)
        sendBtn.setOnClickListener { v: View? ->
            send(
                sendText.text.toString()
            )
        }

        mViewModel.getBookmarkList()
        mViewModel.getSuspectedList()

        mViewModel.detailsListPresent.observe(viewLifecycleOwner, Observer {
            if (it==-1) {
                suspected_count.text = "0"
                total_count.text = "0"
            }
        })

        controlLines = ControlLines(view)
        return view
    }

    /*
     * Serial + UI
     */
    private fun connect(permissionGranted: Boolean? = null) {
        var device: UsbDevice? = null
        val usbManager =
            activity!!.getSystemService(Context.USB_SERVICE) as UsbManager
        for (v in usbManager.deviceList.values) if (v.deviceId == deviceId) device =
            v
        if (device == null) {
            status("connection failed: device not found")
            return
        }
        var driver = UsbSerialProber.getDefaultProber().probeDevice(device)
        if (driver == null) {
            driver = CustomProber.customProber.probeDevice(device)
        }
        if (driver == null) {
            status("connection failed: no driver for device")
            return
        }
        if (driver.ports.size < portNum) {
            status("connection failed: not enough ports at device")
            return
        }
        usbSerialPort = driver.ports[portNum]
        val usbConnection = usbManager.openDevice(driver.device)
        if (usbConnection == null && permissionGranted == null && !usbManager.hasPermission(
                driver.device
            )
        ) {
            val usbPermissionIntent = PendingIntent.getBroadcast(
                activity,
                0,
                Intent(Constants.INTENT_ACTION_GRANT_USB),
                0
            )
            usbManager.requestPermission(driver.device, usbPermissionIntent)
            return
        }
        if (usbConnection == null) {
            if (!usbManager.hasPermission(driver.device)) status("connection failed: permission denied") else status(
                "connection failed: open failed"
            )
            return
        }
        connected =
            Connected.Pending
        try {
            socket = SerialSocket()
            service?.connect(this, "Connected")
            if(context != null  && usbSerialPort != null){
                socket?.connect(context!!, service, usbConnection, usbSerialPort!!, baudRate)
            }
            // usb connect is not asynchronous. connect-success and connect-error are returned immediately from socket.connect
            onSerialConnect()
        } catch (e: Exception) {
            onSerialConnectError(e)
        }
    }

    private fun disconnect() {
        connected =
            Connected.False
        controlLines!!.stop()
        service?.disconnect()
        socket?.disconnect()
        socket = null
        usbSerialPort = null
    }

    private fun send(str: String) {
        if (connected != Connected.True) {
            Toast.makeText(activity, "not connected", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val spn = SpannableStringBuilder(str + '\n')
            spn.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.colorSendText)),
                0,
                spn.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            //receive_text!!.append(spn)
            val data = (str + newline).toByteArray()
            socket?.write(data)
        } catch (e: Exception) {
            onSerialIoError(e)
        }
    }

    private fun checkSettings(): Boolean {
        SharedPreferencesHolder.initializeSharedPrefs(context!!)
        return SharedPreferencesHolder.appSharedPreferences.getBoolean(Constants.SHARED_PREF_KEY_SETTING_DATA, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
            .load(R.drawable.thermometer_place)
            .into(imgview_forehead)
        showForeheadImg(true)
        isSoundEnabled = checkSettings()
        camera_btn.visibility = View.GONE
        cancel_btn.visibility = View.GONE
        total_lyt?.setOnClickListener{
            showDetails(false)
        }
        suspected_lyt?.setOnClickListener{
            showDetails(true)
        }
        mViewModel.detailsList.observe(viewLifecycleOwner , Observer {
            total_count.text = it.size.toString()
        })
        mViewModel.suspectedList.observe(viewLifecycleOwner , Observer {
            suspected_count.text = it.size.toString()
        })
        mViewModel.isCleared.observe(viewLifecycleOwner , Observer {
            if(it==1){
                Toast.makeText(context, "Session Cleared Successfully", Toast.LENGTH_LONG).show()
                suspected_count.text = "0"
                total_count.text = "0"
            }
        })
        reset_txt?.setOnClickListener {
            mViewModel.clearDB()
        }
    }

    private fun logAnalyticsEvent(total: String, suspected: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, total)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, suspected)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        MainApplication.mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    private fun showDetails(b: Boolean) {
        val args = Bundle()
        args.putBoolean("isSuspected", b)
        val fragment: Fragment =
            DetailsFragment()
        fragment.arguments = args
        fragmentManager!!.beginTransaction().replace(R.id.fragment, fragment, "Details")
            .addToBackStack("Details").commit()
    }


    private fun receive(data: ByteArray) {
            if(String(data).trim().equals("position_error") && !isSuspected ){
                    showForeheadImg(true)
                    camera_btn?.visibility = View.GONE
                    cancel_btn?.visibility = View.GONE
                    temp_bg?.setBackgroundResource(R.drawable.temperature_bg)
                    mDataCounter = 0

            }else{
                if(mDataCounter < 3) {
                    mDataCounter += 1
                    var tempValue = 0f
                    try {
                        tempValue = String(data).toFloat()
                        receive_text.text = String(data).trim()
                        showForeheadImg(false)
                        if (mDataCounter > 2) {
                            mData = String(data)
                            isSuspected = checkTempForCamera(tempValue)
                            if(!isSuspected)saveData(String(data), "")
                            playSound(isSuspected)
                            Thread.sleep(1500);
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
            }

    }

    private fun saveData(tempValue: String, info: String) {
        var tableItem = DetailsTable()
        tableItem.temperature = tempValue
        tableItem.isSuspected = isSuspected
        tableItem.date = Calendar.getInstance().timeInMillis

        val pattern = "hh:mm a"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val time: String = simpleDateFormat.format(Calendar.getInstance().getTime())
        tableItem.time = time

        val pattern1 = "dd/MM/yy"
        val simpleDateFormat1 = SimpleDateFormat(pattern1)
        val time1: String = simpleDateFormat1.format(Calendar.getInstance().getTime())
        tableItem.lastDate = time1
        tableItem.imgPath = info
        mViewModel.saveToBookmark(tableItem)
    }

    private fun checkTempForCamera(tempValue: Float): Boolean {
        if(tempValue > 38f){
            temp_bg?.setBackgroundResource(R.drawable.temp_red_bg)
            camera_btn.visibility = View.VISIBLE
            cancel_btn.visibility = View.VISIBLE
            camera_btn.setOnClickListener {
                launchCamera()
            }
            cancel_btn.setOnClickListener {
                saveData(mData,"")
                isSuspected = false
            }
            return true
        }else{
            camera_btn.visibility = View.GONE
            cancel_btn.visibility = View.GONE
            temp_bg?.setBackgroundResource(R.drawable.temp_green_bg)
            return false
        }
    }

    private fun launchCamera() {
        val intent = Intent(context, CameraActivity::class.java)
        startActivityForResult(intent, CAMERA_INTENT)
    }

    private fun playSound(suspected: Boolean?) {
        var mp1 : MediaPlayer? = null
        suspected?.let {
            if(suspected) mp1 = MediaPlayer.create(context, R.raw.suspected)
            else mp1 = MediaPlayer.create(context, R.raw.normal)
        }
        if(suspected==null) mp1 = MediaPlayer.create(context, R.raw.scan)
        mp1?.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
            @Override
            override fun onCompletion(mp: MediaPlayer?) {
                mp?.reset()
                mp?.release()
                mp1 = null
            }
        });
        if(isSoundEnabled) mp1?.start()
    }

    private fun showForeheadImg(b: Boolean) {
        if(b){
            forehead_lyt?.visibility = View.VISIBLE
        }else{
            forehead_lyt?.visibility = View.GONE
        }
    }

    fun status(str: String) {
        val spn = SpannableStringBuilder(str + '\n')
        spn.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorStatusText)),
            0,
            spn.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //receive_text?.append(spn)
        if(str.contains("connection lost")||str.contains("connection failed")){
            (activity as MainActivity).onTerminalBackPressed()
        }
    }

    /*
     * SerialListener
     */
    override fun onSerialConnect() {
        status("connected")
        connected =
            Connected.True
        controlLines!!.start()
    }

    override fun onSerialConnectError(e: Exception?) {
        status("connection failed: " + e?.message)
        disconnect()
    }

    override fun onSerialRead(data: ByteArray?) {
        data?.let { receive(it) }
    }

    override fun onSerialIoError(e: Exception?) {
        status("connection lost: " + e?.message)
        disconnect()
    }

    internal inner class ControlLines(view: View) {
        private val mainLooper: Handler
        private val runnable: Runnable
        private val rtsBtn: ToggleButton
        private val ctsBtn: ToggleButton
        private val dtrBtn: ToggleButton
        private val dsrBtn: ToggleButton
        private val cdBtn: ToggleButton
        private val riBtn: ToggleButton
        private fun toggle(v: View) {
            val btn = v as ToggleButton
            if (connected != Connected.True) {
                btn.isChecked = !btn.isChecked
                Toast.makeText(activity, "not connected", Toast.LENGTH_SHORT).show()
                return
            }
            var ctrl = ""
            try {
                if (btn == rtsBtn) {
                    ctrl = "RTS"
                    usbSerialPort!!.rts = btn.isChecked
                }
                if (btn == dtrBtn) {
                    ctrl = "DTR"
                    usbSerialPort!!.dtr = btn.isChecked
                }
            } catch (e: IOException) {
                status("set" + ctrl + " failed: " + e.message)
            }
        }

        private fun refresh(): Boolean {
            var ctrl = ""
            try {
                ctrl = "RTS"
                rtsBtn.isChecked = usbSerialPort!!.rts
                ctrl = "CTS"
                ctsBtn.isChecked = usbSerialPort!!.cts
                ctrl = "DTR"
                dtrBtn.isChecked = usbSerialPort!!.dtr
                ctrl = "DSR"
                dsrBtn.isChecked = usbSerialPort!!.dsr
                ctrl = "CD"
                cdBtn.isChecked = usbSerialPort!!.cd
                ctrl = "RI"
                riBtn.isChecked = usbSerialPort!!.ri
            } catch (e: IOException) {
                status("get" + ctrl + " failed: " + e.message + " -> stopped control line refresh")
                return false
            }
            return true
        }

        fun start() {
            if (connected == Connected.True && refresh()) mainLooper.postDelayed(
                runnable,
                refreshInterval.toLong()
            )
        }

        fun stop() {
            mainLooper.removeCallbacks(runnable)
        }

        init {
            mainLooper = Handler(Looper.getMainLooper())
            runnable =
                Runnable { start() } // w/o explicit Runnable, a new lambda would be created on each postDelayed, which would not be found again by removeCallbacks
            rtsBtn = view.findViewById(R.id.controlLineRts)
            ctsBtn = view.findViewById(R.id.controlLineCts)
            dtrBtn = view.findViewById(R.id.controlLineDtr)
            dsrBtn = view.findViewById(R.id.controlLineDsr)
            cdBtn = view.findViewById(R.id.controlLineCd)
            riBtn = view.findViewById(R.id.controlLineRi)
            rtsBtn.setOnClickListener { v: View ->
                toggle(
                    v
                )
            }
            dtrBtn.setOnClickListener { v: View ->
                toggle(
                    v
                )
            }
        }
    }

    init {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                if (intent.action == Constants.INTENT_ACTION_GRANT_USB) {
                    val granted = intent.getBooleanExtra(
                        UsbManager.EXTRA_PERMISSION_GRANTED, false
                    )
                    connect(granted)
                }
            }
        }
    }
    companion object {
        private const val refreshInterval = 200 // msec
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CAMERA_INTENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val info = data?.extras?.getString(RESULT_INTENT)
                    val width = data?.extras?.getInt(INTENT_KEY_VIEWFINDERWIDTH)
                    val height = data?.extras?.getInt(INTENT_KEY_VIEWFINDERHEIGHT)
                    val imgFile = File(info as String)
                    if (imgFile.exists()) {
//                        val bitmapFile = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
//                        val matrix = Matrix()
//                        matrix.postRotate(90f)
//                        val previewBitmap =
//                            Bitmap.createScaledBitmap(bitmapFile, bitmapFile.height, bitmapFile.width, true)
//                        val scaledBitmap = Bitmap.createScaledBitmap(bitmapFile, height!!, width!!, true)
//                        val rotatedScaledBitmap = Bitmap.createBitmap(
//                            scaledBitmap,
//                            0,
//                            0,
//                            scaledBitmap.width,
//                            scaledBitmap.height,
//                            matrix,
//                            true
//                        )
//                        val rotatedBitmap = Bitmap.createBitmap(
//                            previewBitmap,
//                            0,
//                            0,
//                            previewBitmap.width,
//                            previewBitmap.height,
//                            matrix,
//                            true
//                        )
                        Toast.makeText(context, "Capture Success", Toast.LENGTH_LONG).show()

                    }
                    saveData(mData,info)
                    isSuspected = false
                    camera_btn.visibility = View.GONE
                    cancel_btn.visibility = View.GONE
                }
                else if(resultCode == Activity.RESULT_CANCELED){
                    saveData(mData,"")
                    isSuspected = false
                    camera_btn.visibility = View.GONE
                    cancel_btn.visibility = View.GONE
                    Toast.makeText(context, "Capture Failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun popFragments() {
        fragmentManager!!.popBackStack(fragmentManager!!.getBackStackEntryAt(fragmentManager!!.getBackStackEntryCount()-2).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


}
