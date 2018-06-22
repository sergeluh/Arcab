package com.serg.arcab.base

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.serg.arcab.LoadingUiHelper

abstract class BaseActivity: AppCompatActivity() {

    private var progressDialog: LoadingUiHelper.ProgressDialogFragment? = null

    fun showLoading(type : LoadingUiHelper.Type = LoadingUiHelper.Type.FULL_SCREEN) {
        if(progressDialog == null) {
            progressDialog = LoadingUiHelper.showProgress(supportFragmentManager, type)
        }
    }

    fun hideLoading() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun showMessage(message: String?) {
        message?.also {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}