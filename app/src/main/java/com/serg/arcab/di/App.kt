package com.serg.arcab.di

import com.serg.arcab.PlacesManager
import com.serg.arcab.data.AppExecutors
import com.serg.arcab.datamanager.UserDataManager
import com.serg.arcab.datamanager.UserDataManagerImpl
import com.serg.arcab.ui.auth.AuthViewModel
import com.serg.arcab.ui.main.MainViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

private val appModule = applicationContext {
    bean { AppExecutors() }
}

private val viewModelModule = applicationContext {
    viewModel { AuthViewModel(get()) }
    viewModel { MainViewModel() }
}

private val dataManagerModule = applicationContext {
    bean { UserDataManagerImpl(get()) as UserDataManager }
}

private val helperModule = applicationContext {
    factory { PlacesManager(get()) }
}

val moduleList = listOf(appModule, dataManagerModule, viewModelModule, helperModule)