package com.circuitloop.easyscan.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.circuitloop.easyscan.R
import com.circuitloop.easyscan.utils.Constants
import com.circuitloop.easyscan.utils.SharedPreferencesHolder
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkSettings()
        switch1.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            saveData(isChecked)
        })
    }



}
