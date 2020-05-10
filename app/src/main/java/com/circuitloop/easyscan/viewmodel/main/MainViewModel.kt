package com.circuitloop.easyscan.viewmodel.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.circuitloop.easyscan.database.DetailsTable
import com.circuitloop.easyscan.database.DetailsTableDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainViewModel(var movieTableDao: DetailsTableDao) : ViewModel() {

    var detailsList: MutableLiveData<List<DetailsTable>> = MutableLiveData()
    var suspectedList: MutableLiveData<List<DetailsTable>> = MutableLiveData()
    var detailsListPresent: MutableLiveData<Int> = MutableLiveData()
    var isAdded: MutableLiveData<Int> = MutableLiveData()
    var isCleared: MutableLiveData<Int> = MutableLiveData()

    fun getBookmarkList() {
        movieTableDao.getAllList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it != null && it?.size > 0) {
                    detailsList.postValue(it)
                } else {
                    detailsListPresent.postValue(-1)
                }
            }
    }

    fun getSuspectedList() {
        movieTableDao.getSuspectedList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it != null && it?.size > 0) {
                    suspectedList.postValue(it)
                } else {
                    detailsListPresent.postValue(-1)
                }
            }
    }

    fun saveToBookmark(detail: DetailsTable) {
        movieTableDao.add(detail).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                isAdded.value = 1
            }

    }

    fun clearDB() {
        movieTableDao.clearAllTables().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                detailsList.postValue(listOf())
                suspectedList.postValue(listOf())
                isCleared.postValue(1)
            }
    }

}