package com.serg.arcab

import android.app.Application
import android.util.Log
import com.serg.arcab.di.moduleList
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import java.net.ConnectException

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
        startKoin(this, moduleList)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                        return
                    }
                    if (t != null && t !is ConnectException) {
                        try {
//                            Crashlytics.logException(t)
//                            FirebaseCrash.report(t)
                        } catch (ignore: Exception) {
                            //ignore
                        }
                    }
                }
            })
        }
    }
}