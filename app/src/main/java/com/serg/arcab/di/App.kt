package com.serg.arcab.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places
import com.serg.arcab.LocationManager
import com.serg.arcab.PREFS_NAME
import com.serg.arcab.PlacesManager
import com.serg.arcab.data.AppExecutors
import com.serg.arcab.data.PrefsManager
import com.serg.arcab.data.PrefsManagerImpl
import com.serg.arcab.datamanager.AuthDataManager
import com.serg.arcab.datamanager.AuthDataManagerImpl
import com.serg.arcab.ui.auth.AuthViewModel
import com.serg.arcab.ui.main.MainViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

private val appModule = applicationContext {
    bean { AppExecutors() }
    bean {get<Context>().getSharedPreferences(PREFS_NAME, MODE_PRIVATE)}
}

private val viewModelModule = applicationContext {
    viewModel { AuthViewModel(get()) }
    viewModel { MainViewModel(get()) }
}

private val dataManagerModule = applicationContext {
    bean { AuthDataManagerImpl(get(), get()) as AuthDataManager }
    bean { PrefsManagerImpl(get()) as PrefsManager }
}

private val helperModule = applicationContext {
    factory { Places.getGeoDataClient(get<Context>()) as GeoDataClient }
    factory { PlacesManager(get(), get(), get()) }
    factory { LocationManager(get()) }
}

val moduleList = listOf(appModule, dataManagerModule, viewModelModule, helperModule)