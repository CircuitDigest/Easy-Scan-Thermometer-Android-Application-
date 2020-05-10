package com.circuitloop.easyscan.view.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.circuitloop.easyscan.R
import kotlinx.android.synthetic.main.fragment_terminal.*

class SampleFrag : Fragment() {

    companion object {
        fun newInstance() =
            SampleFrag()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_terminal, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
            .load(R.drawable.thermometer_place)
            .into(imgview_forehead)
    }

}
