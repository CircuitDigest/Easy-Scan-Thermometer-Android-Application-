package com.circuitloop.easyscan.view.main.fragment

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assignment.ui.adapter.BaseRecyclerAdapter
import com.circuitloop.easyscan.R
import com.circuitloop.easyscan.database.DetailsTable
import com.circuitloop.easyscan.databinding.FragmentDetailsBinding
import com.circuitloop.easyscan.utils.*
import com.circuitloop.easyscan.viewmodel.main.MainViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.cutom_date_picker.*
import kotlinx.android.synthetic.main.fragment_details.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File


private const val REQUEST_CODE_PERMISSIONS = 199
private const val REQUEST_CODE_SHARE_PERMISSIONS = 200
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

class DetailsFragment : Fragment(),ApplicationHandler, AdapterView.OnItemSelectedListener {

    private var isFiltered: Boolean = false
    private var filterList = listOf<DetailsTable>()
    private var isSuspected: Boolean = false
    lateinit var fragmentDetailsBinding: FragmentDetailsBinding
    var baseRecyclerAdapter: BaseRecyclerAdapter? = null
    val mViewModel: MainViewModel by viewModel()
    lateinit var list : List<DetailsTable>
    lateinit var  toast: Toast
    companion object {
        fun newInstance() =
            DetailsFragment()
        lateinit var recyclerView : RecyclerView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        arguments?.getBoolean("isSuspected",false)?.let {
            isSuspected = it
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDetailsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_details,
            container,
            false
        )
        initAdapter()
        recyclerView = fragmentDetailsBinding.recyclerView
        toast = Toast.makeText(context, "Save Success", Toast.LENGTH_LONG)
        if(isSuspected){
            mViewModel.getSuspectedList()
        }else{
            mViewModel.getBookmarkList()
        }
        mViewModel.detailsListPresent.observe(viewLifecycleOwner, Observer {
            fragmentDetailsBinding.progress.visibility = View.GONE
            fragmentDetailsBinding.dateSpinner.visibility = View.GONE
            fragmentDetailsBinding.noItems.visibility = View.VISIBLE
            fragmentDetailsBinding.txtTitle.visibility = View.GONE
            fragmentDetailsBinding.recyclerView.visibility = View.GONE
            fragmentDetailsBinding.dateSpinner.visibility = View.GONE
            fragmentDetailsBinding.floatingActionButton.hide()
        })

        if(isSuspected){
            mViewModel.suspectedListPresent.observe(viewLifecycleOwner, Observer {
                fragmentDetailsBinding.progress.visibility = View.GONE
                fragmentDetailsBinding.dateSpinner.visibility = View.GONE
                fragmentDetailsBinding.noItems.visibility = View.VISIBLE
                fragmentDetailsBinding.txtTitle.visibility = View.GONE
                fragmentDetailsBinding.recyclerView.visibility = View.GONE
                fragmentDetailsBinding.dateSpinner.visibility = View.GONE
                fragmentDetailsBinding.floatingActionButton.hide()
            })
        }
        return fragmentDetailsBinding.root
    }

    private fun initAdapter() {
        if(isSuspected){
            baseRecyclerAdapter = BaseRecyclerAdapter(R.layout.item_suspected_list,this)
        }else{
            baseRecyclerAdapter = BaseRecyclerAdapter(R.layout.item_details,this)
        }
        fragmentDetailsBinding.recyclerView.setHasFixedSize(true);
        fragmentDetailsBinding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        fragmentDetailsBinding.recyclerView.adapter = baseRecyclerAdapter
        fragmentDetailsBinding.progress.visibility = View.VISIBLE
        fragmentDetailsBinding.noItems.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentDetailsBinding.recyclerView.visibility = View.VISIBLE
        floating_action_button.setOnClickListener {
            handleShareClick()
        }
        if(isSuspected){
            if (allPermissionsGranted()) {
                callSuspectedInit()
            } else {
                requestPermissions( //Method of Fragment
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }

        }else{
            mViewModel.detailsList.observe(viewLifecycleOwner , Observer {
                callFilterCalculation(it)
                fragmentDetailsBinding.dateSpinner.visibility = View.VISIBLE
                fragmentDetailsBinding.progress.visibility = View.GONE
                fragmentDetailsBinding.noItems.visibility = View.GONE
                baseRecyclerAdapter?.updateList(it)
                fragmentDetailsBinding.txtTitle.text = "Scanned List"
                fragmentDetailsBinding.txtTitle.visibility = View.VISIBLE
                fragmentDetailsBinding.floatingActionButton.show()
                list = it
            })
        }

        mViewModel.filterList.observe(viewLifecycleOwner , Observer {
            baseRecyclerAdapter?.updateList(it)
            filterList = it
        })


    }

    private fun callSuspectedInit() {
        mViewModel.suspectedList.observe(viewLifecycleOwner , Observer {
            callFilterCalculation(it)
            fragmentDetailsBinding.dateSpinner.visibility = View.VISIBLE
            fragmentDetailsBinding.progress.visibility = View.GONE
            fragmentDetailsBinding.noItems.visibility = View.GONE
            baseRecyclerAdapter?.updateList(it)
            fragmentDetailsBinding.txtTitle.text = "Suspected List"
            fragmentDetailsBinding.txtTitle.visibility = View.VISIBLE
            list = it
            fragmentDetailsBinding.floatingActionButton.show()
        })
    }

    private fun callFilterCalculation(list: List<DetailsTable>) {
        for(i in 0 until list.size){
            if(i > 0){
                if(list?.get(i-1)?.lastDate != list?.get(i)?.lastDate){
                    context?.let {
                        saveValue(it, list?.get(i)?.lastDate!!, i)
                    }
                }
            }else{
                context?.let {
                    saveValue(it, list?.get(i)?.lastDate!!, i)
                }
            }
        }

        context?.let {
            var list = ArrayList<String>()
            list.add("All")
            list.addAll(getList(context!!,true))
            val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(context!!, R.layout.simple_spinner_item, list)
            dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            fragmentDetailsBinding.dateSpinner.setAdapter(dataAdapter)
            fragmentDetailsBinding.dateSpinner.setOnItemSelectedListener(this)
        }
    }

    private fun saveValue(context: Context, text : String, pos: Int?) {
            SharedPreferencesHolder.initializeSharedPrefs(context)
            var list = getList(context, true)
            var intList = getList(context, false)
            if(pos == 0){
                list.add("All")
                list.clear()
                intList.clear()
            }
            list.add(text)
            intList.add(pos.toString())
            with (SharedPreferencesHolder.appSharedPreferences.edit()) {
                putString(Constants.SHARED_PREF_KEY_LIST_DATA, ObjectSerialiser.serialize(list))
                putString(Constants.SHARED_PREF_KEY_LIST_INT_DATA, ObjectSerialiser.serialize(intList))
                apply()
                commit()
            }
    }



    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        this.context?.let {it1 ->
            checkSelfPermission(
                it1, it
            ) == PackageManager.PERMISSION_GRANTED
        } ?: return false

    }

    private fun handleShareClick() {
        if (allPermissionsGranted()) {
            fragmentDetailsBinding.progress.visibility = View.VISIBLE
            fragmentDetailsBinding.blurView.visibility = View.VISIBLE
            if(isFiltered){
                Single.fromCallable<ObservableField<String>> { WriteExcel.main(filterList, isSuspected) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { resultObject -> handleDBSucess(resultObject) }
            }else{
                Single.fromCallable<ObservableField<String>> { WriteExcel.main(list, isSuspected) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { resultObject -> handleDBSucess(resultObject) }
            }

        }else{
                requestPermissions( //Method of Fragment
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_SHARE_PERMISSIONS
                )

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                callSuspectedInit()
            } else {
                callSuspectedInit()
                Toast.makeText(
                    this.context,
                    "Permissions not granted by the user, Pictures may not be Visible",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else if (requestCode == REQUEST_CODE_SHARE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                fragmentDetailsBinding.progress.visibility = View.VISIBLE
                fragmentDetailsBinding.blurView.visibility = View.VISIBLE
                Single.fromCallable<ObservableField<String>> { WriteExcel.main(list, isSuspected) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { resultObject -> handleDBSucess(resultObject) }
            } else {
                Toast.makeText(
                    this.context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun handleDBSucess(resultObject: ObservableField<String>) {
        fragmentDetailsBinding.blurView.visibility = View.GONE
        fragmentDetailsBinding.progress.visibility = View.GONE
        //toast.show()

        val intentShareFile = Intent(Intent.ACTION_SEND)
        val fileWithinMyDir = File(resultObject.get())

        if (fileWithinMyDir.exists() && context != null) {
            intentShareFile.type = "application/pdf"
            intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context!!,  context!!.getApplicationContext().getPackageName() + ".provider",  fileWithinMyDir))
            intentShareFile.putExtra(
                Intent.EXTRA_SUBJECT,
                "Sharing the Scanned Report from EasyScan App"
            )
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...")
            startActivity(Intent.createChooser(intentShareFile, "Share File"))
        }
    }

    override fun onClick(view: View?, data: Any?, position: Any?) {
        when(view?.id){
            R.id.date_txt -> {
                showDialog()
            }
        }
    }

    private fun showDialog() {
        context?.let {
            val dialog = Dialog(context!!)
            dialog.setContentView(R.layout.cutom_date_picker)
            setDialogAdapter(dialog)
            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.show()
        }
    }

    override fun onItemSelected(
        parent: AdapterView<*>,
        view: View?,
        position: Int,
        id: Long
    ) { // On selecting a spinner item
        val item = parent.getItemAtPosition(position).toString()
        if(item.trim().equals("All")){
            filterList = emptyList()
            isFiltered = false
            baseRecyclerAdapter?.updateList(list)
        }else{
            isFiltered = true
            mViewModel.getFilterListByDate(item.trim())
        }
        // Showing selected spinner item
        //Toast.makeText(parent.context, "Selected: $item", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {

    }

    private fun setDialogAdapter(dialog: Dialog) {
        var list = getList(context!!,true)
//        list.add("10/05/20")
//        list.add("11/05/20")
//        list.add("12/05/20")
//        list.add("13/05/20")
//        list.add("14/05/20")
//        list.add("15/05/20")
//        list.add("16/05/20")
//        list.add("17/05/20")
//        list.add("18/05/20")
//        list.add("19/05/20")
//        list.add("20/05/20")
//        list.add("21/05/20")
//        list.add("22/05/20")
//        list.add("23/05/20")
//        list.add("24/05/20")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            context!!,
            R.layout.dialog_list_item, R.id.text1, list
        )
        dialog.listView.setAdapter(adapter)
        if(list.size > 3){
            var item : View ? = adapter.getView(0, null, listView)
            item?.measure(0, 0)
            val params: ConstraintLayout.LayoutParams =
                ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    (3.1 * item?.getMeasuredHeight()!!).toInt()
                )
            dialog.listView.layoutParams = params
        }


        dialog.listView.setOnItemClickListener { adapterView, view, position, l ->
            // TODO Auto-generated method stub
            val value: String? = adapter.getItem(position)
            Toast.makeText(context, value, Toast.LENGTH_SHORT).show()
        }
    }


    private fun getList(context: Context, isDate: Boolean): ArrayList<String> {
        SharedPreferencesHolder.initializeSharedPrefs(context)
        var list = ArrayList<String>()

        if(isDate){
            var temp = SharedPreferencesHolder.appSharedPreferences.getString(Constants.SHARED_PREF_KEY_LIST_DATA, "")
            if(temp!!.isNotEmpty()){
                try{
                    list = ObjectSerialiser.deserialize(temp) as ArrayList<String>
                }catch (e: Exception){

                }
            }
        }else{
            var temp = SharedPreferencesHolder.appSharedPreferences.getString(Constants.SHARED_PREF_KEY_LIST_INT_DATA, "")
            if(temp!!.isNotEmpty()){
                try{
                    list = ObjectSerialiser.deserialize(temp) as ArrayList<String>
                }catch (e: Exception){

                }
            }
        }

        return list
    }


}
