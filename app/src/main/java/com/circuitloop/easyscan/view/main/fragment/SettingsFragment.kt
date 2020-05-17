package com.circuitloop.easyscan.view.main.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.circuitloop.easyscan.R
import com.circuitloop.easyscan.utils.Constants
import com.circuitloop.easyscan.utils.SharedPreferencesHolder
import com.circuitloop.easyscan.view.main.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() =
            SettingsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)
        return view
    }

    private fun checkSettings() {
        SharedPreferencesHolder.initializeSharedPrefs(context!!)
        switch1.isChecked = SharedPreferencesHolder.appSharedPreferences.getBoolean(Constants.SHARED_PREF_KEY_SETTING_DATA, true)
    }

    private fun saveData(checked: Boolean) {
        SharedPreferencesHolder.initializeSharedPrefs(context!!)
        with (SharedPreferencesHolder.appSharedPreferences.edit()) {
            putBoolean(Constants.SHARED_PREF_KEY_SETTING_DATA, checked)
            apply()
            commit()
        }
    }

    private fun checkStorageTempValue(): Float {
        SharedPreferencesHolder.initializeSharedPrefs(context!!)
        return SharedPreferencesHolder.appSharedPreferences.getFloat(Constants.SHARED_PREF_KEY_TEMP, 38f)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkSettings()
        switch1.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            saveData(isChecked)
        })
        edit_txt.setText(checkStorageTempValue().toString())
        edit_txt.setOnKeyListener { v, keyCode, event ->
            // If the event is a key-down event on the "enter" button
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Perform action on key press
                (activity as MainActivity).hideKeyboard(activity!!)
                v.clearFocus()
                SharedPreferencesHolder.initializeSharedPrefs(v.context)
                with (SharedPreferencesHolder.appSharedPreferences.edit()) {
                    putFloat(Constants.SHARED_PREF_KEY_TEMP, edit_txt.text.toString().toFloat())
                    apply()
                    commit()
                }
                true
            }
            false
        }

    }



}
