package com.circuitloop.easyscan.view.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.circuitloop.easyscan.R
import com.circuitloop.easyscan.databinding.FragmentHomeBinding
import com.circuitloop.easyscan.viewmodel.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_terminal.*
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    lateinit var fragmentHomeBinding: FragmentHomeBinding

    companion object {
        fun newInstance() =
            HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentHomeBinding.measureBtn.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.fragment, MainFragment.newInstance(), "Main")
                .addToBackStack("Main").commit()
        }
        fragmentHomeBinding.dbBtn.setOnClickListener {
            val args = Bundle()
            args.putBoolean("isSuspected", false)
            val fragment: Fragment =
                DetailsFragment()
            fragment.arguments = args
            fragmentManager!!.beginTransaction().replace(R.id.fragment, fragment, "Details")
                .addToBackStack("Details").commit()
        }
        fragmentHomeBinding.settingBtn.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.fragment, SettingsFragment.newInstance(), "Settings")
                .addToBackStack("Settings").commit()
        }
    }

}
