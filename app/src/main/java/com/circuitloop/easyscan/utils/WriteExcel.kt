package com.circuitloop.easyscan.utils

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.circuitloop.easyscan.database.DetailsTable
import jxl.CellView
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableImage
import jxl.write.WritableWorkbook
import jxl.write.WriteException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*


/**
 * Created by Valliyappan M on 08-May-20.
 */


object WriteExcel {

    var parent = File(Environment.getExternalStorageDirectory().path,"/data/EasyScan")
    private var EXCEL_FILE_LOCATION = "/sdcard/data/EasyScan/ScannedReport_${System.currentTimeMillis()}.xls"
    var temp = ObservableField<String>("")
    @JvmStatic
    fun main(
        list: List<DetailsTable>,
        isSuspected: Boolean
    ): ObservableField<String> { //1. Create an Excel file

        if(!parent.isDirectory){
            parent.mkdirs()
        }
        var myFirstWbook: WritableWorkbook? = null
        try {
            myFirstWbook = Workbook.createWorkbook(File(EXCEL_FILE_LOCATION))
            // create an Excel sheet
            val excelSheet = myFirstWbook.createSheet("Sheet 1", 0)
            // add something into the Excel sheet
            var label = Label(0, 0, "Serial No")
            excelSheet.addCell(label)
            label = Label(1, 0, "Temperature")
            excelSheet.addCell(label)
            label = Label(2, 0, "Scanned Time")
            excelSheet.addCell(label)
            label = Label(3, 0, "Scanned Date")
            excelSheet.addCell(label)
            label = Label(4, 0, "Scanned Image")
            excelSheet.addCell(label)
            val columnAView = CellView()
            columnAView.isAutosize = true
            for (i in 0 until list.size) {
                label = Label(0, i+1, list[i].serialNo.toString())
                excelSheet.addCell(label)
                label = Label(1, i+1, list[i].temperature.toString())
                excelSheet.addCell(label)
                label = Label(2, i+1, list[i].time.toString())
                excelSheet.addCell(label)
                label = Label(3, i+1, list[i].lastDate.toString())
                excelSheet.addCell(label)
                excelSheet.setColumnView(0, columnAView)
                excelSheet.setColumnView(1, columnAView)
                excelSheet.setColumnView(2, columnAView)
                excelSheet.setColumnView(3, columnAView)
                    if(File(list[i].imgPath).isFile){
                        var bp = BitmapFactory.decodeFile(list[i].imgPath)
                        var matrix = Matrix();
                        matrix.postRotate(90f);
                        val stream = ByteArrayOutputStream()
                        var bmp = Bitmap.createBitmap(bp, 0, 0, bp.getWidth(), bp.getHeight(), matrix, true)
                        bmp!!.compress(CompressFormat.JPEG, 50, stream)
                        var temp = WritableImage(4.0,i.toDouble()+1,1.0,1.0,stream.toByteArray())
                        excelSheet.addImage(temp)
                    }
                    excelSheet.setColumnView(4, columnAView)

                excelSheet.setRowView(i+1,1500)
            }
            myFirstWbook.write()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: WriteException) {
            e.printStackTrace()
        } finally {
            if (myFirstWbook != null) {
                try {
                    myFirstWbook.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: WriteException) {
                    e.printStackTrace()
                }
            }
            temp.set(EXCEL_FILE_LOCATION)
            return temp
        }
    }
}