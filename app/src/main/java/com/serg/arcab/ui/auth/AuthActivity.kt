package com.serg.arcab.ui.auth

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.serg.arcab.R
import com.serg.arcab.base.BaseActivity
import org.koin.android.architecture.ext.viewModel

class AuthActivity : BaseActivity() {

    private val viewModel by viewModel<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) {
            replaceFragment(AuthFragment.newInstance(), AuthFragment.TAG)
        }

        viewModel.goToMobileNumberLogin.observe(this, Observer {
            replaceFragment(PhoneFragment.newInstance(), PhoneFragment.TAG)
        })

        viewModel.goToSocialLogin.observe(this, Observer {

        })
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commitNow()
    }

    companion object {

        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, AuthActivity::class.java))
        }
    }
}