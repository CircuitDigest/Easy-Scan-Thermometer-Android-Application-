package com.circuitloop.easyscan.view.main.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import com.circuitloop.easyscan.R
import com.circuitloop.easyscan.app.MainApplication
import com.circuitloop.easyscan.database.DetailsTable
import com.circuitloop.easyscan.utils.SharedPreferencesHolder
import com.circuitloop.easyscan.view.main.fragment.HomeFragment
import com.circuitloop.easyscan.view.main.fragment.SettingsFragment
import com.circuitloop.easyscan.view.main.fragment.TerminalFragment
import com.circuitloop.easyscan.viewmodel.main.MainViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog_touch_id_input.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(),LifecycleObserver {

    val mViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, HomeFragment.newInstance())
                    .commitNow()
        }
        reset.setOnClickListener {
//            val intent = Intent(this, CameraActivity::class.java)
//            startActivityForResult(intent, Constants.CAMERA_INTENT)
            showDialog()
            //saveData()
        }
        setting.setOnClickListener {
            if (!(getVisibleFragment() is SettingsFragment)){
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, SettingsFragment.newInstance())
                    .addToBackStack("settings")
                    .commit()

            }

        }
        logo.setOnClickListener {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            for (fragment in supportFragmentManager.fragments) {
                supportFragmentManager.fragments.remove(fragment)
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, HomeFragment.newInstance())
                .commitNow()
        }
        logo_txt.setOnClickListener {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            for (fragment in supportFragmentManager.fragments) {
                supportFragmentManager.fragments.remove(fragment)
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, HomeFragment.newInstance())
                .commitNow()
        }
        mViewModel.isCleared.observe(this , Observer {
            if(it==1){
                Toast.makeText(this, "Session Cleared Successfully", Toast.LENGTH_LONG).show()
            }
        })

        mViewModel.detailsListPresent.observe(this, Observer {
            if (it==-1) {
                Log.d("Valliyappan","None")
            }
        })
    }

    private fun saveData(){
        var tableItem = DetailsTable()
        tableItem.temperature = "42"
        tableItem.isSuspected = true
        tableItem.date = Calendar.getInstance().timeInMillis

        val pattern = "hh:mm a"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val time: String = simpleDateFormat.format(Calendar.getInstance().getTime())
        tableItem.time = time

        val pattern1 = "EEE, LLL dd"
        val simpleDateFormat1 = SimpleDateFormat(pattern1)
        val time1: String = simpleDateFormat1.format(Calendar.getInstance().getTime())
        tableItem.lastDate = time1
        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
        tableItem.serialNo = calculateSerialNo(time1).toString()
        savePrefData(time1,tableItem.serialNo.toInt())
        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112898103.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112940454.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112961754.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112977314.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589112997130.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113014869.jpg"
//        mViewModel.saveToBookmark(tableItem)
//        tableItem.imgPath = "/storage/emulated/0/EasyScan/1589113032564.jpg"
//        mViewModel.saveToBookmark(tableItem)
    }

    private fun calculateSerialNo(
        time: String) : Int {
        SharedPreferencesHolder.initializeFilterSharedPrefs(this)
        return (SharedPreferencesHolder.filterSharedPreferences.getInt(time, 0) + 1)
    }

    private fun savePrefData(date: String,temp : Int) {
        SharedPreferencesHolder.initializeFilterSharedPrefs(this)
        with (SharedPreferencesHolder.filterSharedPreferences.edit()) {
            putInt(date, temp)
            apply()
            commit()
        }
    }


    private fun showDialog() {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.custom_dialog_touch_id_input)
                dialog.txt_positive_btn.setOnClickListener {
                    mViewModel.clearDB()
                    SharedPreferencesHolder.initializeFilterSharedPrefs(this)
                    SharedPreferencesHolder.deleteFilterSharedPrefData()
                    mViewModel.getBookmarkList()
                    dialog.dismiss()
                }
                dialog.txt_negative_btn.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
        logAnalyticsEvent("sample","test")
                dialog.show()
        }

    private fun logAnalyticsEvent(total: String, suspected: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, total)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, suspected)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        MainApplication.mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }
    override fun onNewIntent(intent: Intent) {
        if (intent.action == "android.hardware.usb.action.USB_DEVICE_ATTACHED") {
            (supportFragmentManager.findFragmentByTag("terminal") as TerminalFragment?)?.status("USB device detected")
        }
        super.onNewIntent(intent)
    }

    override fun onBackPressed() {
        if (getVisibleFragment() is TerminalFragment){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            for (fragment in supportFragmentManager.fragments) {
                supportFragmentManager.fragments.remove(fragment)
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, HomeFragment.newInstance())
                .commitNow()
        }else if (getVisibleFragment() is HomeFragment){
            finish()
        }else super.onBackPressed()
    }


    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun getVisibleFragment(): Fragment? {
        val fragmentManager: FragmentManager = this@MainActivity.supportFragmentManager
        val fragments: List<Fragment> = fragmentManager.getFragments()
        for (fragment in fragments) {
            if (fragment != null && fragment.isVisible()) return fragment
        }
        return null
    }

    fun onTerminalBackPressed() {
        super.onBackPressed()
    }

}


