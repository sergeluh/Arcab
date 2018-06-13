package com.serg.arcab.di

import com.serg.arcab.data.AppExecutors
import com.serg.arcab.datamanager.UserDataManager
import com.serg.arcab.datamanager.UserDataManagerImpl
import com.serg.arcab.ui.auth.AuthViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

private val appModule = applicationContext {
    bean { AppExecutors() }
}

private val viewModelModule = applicationContext {
    viewModel { AuthViewModel(get()) }
}

private val dataManagerModule = applicationContext {
    bean { UserDataManagerImpl(get()) as UserDataManager }
}

val moduleList = listOf(appModule, dataManagerModule, viewModelModule)