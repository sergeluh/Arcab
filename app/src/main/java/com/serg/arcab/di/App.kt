package com.serg.arcab.di

import com.google.android.gms.location.places.Places
import com.serg.arcab.LocationManager
import com.serg.arcab.PlacesManager
import com.serg.arcab.data.AppExecutors
import com.serg.arcab.datamanager.AuthDataManager
import com.serg.arcab.datamanager.AuthDataManagerImpl
import com.serg.arcab.ui.auth.AuthViewModel
import com.serg.arcab.ui.main.MainViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

private val appModule = applicationContext {
    bean { AppExecutors() }
}

private val viewModelModule = applicationContext {
    viewModel { AuthViewModel(get()) }
    viewModel { MainViewModel() }
}

private val dataManagerModule = applicationContext {
    bean { AuthDataManagerImpl(get()) as AuthDataManager }
}

private val helperModule = applicationContext {
    factory { Places.getGeoDataClient(get()) }
    factory { PlacesManager(get(), get(), get()) }
    factory { LocationManager(get()) }
}

val moduleList = listOf(appModule, dataManagerModule, viewModelModule, helperModule)