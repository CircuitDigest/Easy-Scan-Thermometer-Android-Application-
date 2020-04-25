package com.circuitloop.easyscan.di

import com.circuitloop.easyscan.viewmodel.main.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}