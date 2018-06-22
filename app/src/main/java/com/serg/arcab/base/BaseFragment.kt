package com.serg.arcab.base

import android.Manifest
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.serg.arcab.LoadingUiHelper
import timber.log.Timber

abstract class BaseFragment: Fragment() {

    private lateinit var baseActivity: BaseActivity

    val viewLifecycleOwner = ViewLifecycleOwner()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.baseActivity = context as BaseActivity
    }

    fun getViewLifeCycle(): Lifecycle {
        return viewLifecycleOwner.lifecycle
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onPause() {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        super.onPause()
    }

    override fun onStop() {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onStop()
    }

    override fun onDestroyView() {
        viewLifecycleOwner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroyView()
    }

    protected fun checkLocationPermissions(): Boolean {
        return baseActivity.checkLocationPermissions()
    }

    protected fun requestLocationPermissions(requestCode: Int) {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), requestCode)
    }



    fun showLoading(type : LoadingUiHelper.Type = LoadingUiHelper.Type.FULL_SCREEN) {
        baseActivity.showLoading(type)
    }

    fun hideLoading() {
        baseActivity.hideLoading()
    }

    fun showMessage(message: String?) {
        baseActivity.showMessage(message)
    }



    class ViewLifecycleOwner : LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)

        override fun getLifecycle(): LifecycleRegistry {
            return lifecycleRegistry
        }
    }
}