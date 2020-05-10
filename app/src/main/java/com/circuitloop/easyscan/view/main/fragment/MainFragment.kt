package com.circuitloop.easyscan.view.main.fragment

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.circuitloop.easyscan.R
import com.circuitloop.easyscan.databinding.FragmentMainBinding
import com.circuitloop.easyscan.utils.CustomProber
import com.circuitloop.easyscan.viewmodel.main.MainViewModel
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList


class MainFragment : Fragment() {

    lateinit var fragmentHomeBinding: FragmentMainBinding
    private val listItems = ArrayList<ListItem>()
    private var baudRate = 19200

    companion object {
        fun newInstance() =
            MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )
        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
            .load(R.drawable.otg_connect)
            .into(img_view);
//        Handler().postDelayed(
//            {
//                fragmentManager?.beginTransaction()?.replace(R.id.fragment, DetailsFragment.newInstance(), "Details")
//                    ?.addToBackStack("Details")?.commit()
//            },1000
//        )
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    fun refresh() {
        val usbManager =
            activity!!.getSystemService(Context.USB_SERVICE) as UsbManager
        val usbDefaultProber = UsbSerialProber.getDefaultProber()
        val usbCustomProber: UsbSerialProber = CustomProber.customProber
        listItems.clear()
        for (device in usbManager.deviceList.values) {
            var driver = usbDefaultProber.probeDevice(device)
            if (driver == null) {
                driver = usbCustomProber.probeDevice(device)
            }
            if (driver != null) {
                val args = Bundle()
                args.putInt("device", device.deviceId)
                args.putString("devicename", device.deviceName)
                args.putInt("port", driver.ports.indices.first)
                args.putInt("baud", baudRate)
                val fragment: Fragment =
                    TerminalFragment()
                fragment.arguments = args
                fragmentManager!!.beginTransaction().replace(R.id.fragment, fragment, "terminal")
                    .addToBackStack(null).commit()
            } else {
                listItems.add(
                    ListItem(
                        device,
                        0,
                        null
                    )
                )
            }
        }
    }

    internal class ListItem(var device: UsbDevice, var port: Int, var driver: UsbSerialDriver?)
    val mViewModel: MainViewModel by viewModel()

}
