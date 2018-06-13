package com.serg.arcab.ui.splash

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.serg.arcab.ui.auth.AuthActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AuthActivity.start(this)
        finish()
    }
}
