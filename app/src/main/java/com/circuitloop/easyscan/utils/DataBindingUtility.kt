package com.circuitloop.easyscan.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.circuitloop.easyscan.R
import com.circuitloop.easyscan.database.DetailsTable
import java.io.File


object DataBindingUtility {

    @JvmStatic
    @BindingAdapter("app:roundedImg")
    fun setRoundedImgUrl(imageView: ImageView?, imgUrl: String?) {
        if(imgUrl.isNullOrEmpty()){
            imageView?.setImageDrawable(imageView.context.getDrawable(R.drawable.person_bg))
        }else if(File(imgUrl).isFile){
            val uri: Uri? = Uri.fromFile(File(imgUrl))
            if(uri != null && imageView != null){
                Glide.with(imageView!!.context)
                    .load(uri)
                    .into(imageView);
            }
        }else{
            imageView?.setImageDrawable(imageView.context.getDrawable(R.drawable.person_bg))
        }
    }

    @JvmStatic
    @BindingAdapter("app:list", "app:pos")
    fun setDateText(textView: TextView?, list: List<DetailsTable>?,pos:Int?) {
        pos?.let{
            textView?.text = list?.get(pos)?.lastDate
            if(list!![it].isFilterList){
                textView?.visibility = View.GONE
            }else {
                if (it > 0) {
                    if (list?.get(pos - 1)?.lastDate == list?.get(pos)?.lastDate) {
                        textView?.visibility = View.GONE
                    } else {
                        textView?.visibility = View.VISIBLE
                    }
                } else {
                    textView?.visibility = View.VISIBLE
                }
            }
        }
    }

}