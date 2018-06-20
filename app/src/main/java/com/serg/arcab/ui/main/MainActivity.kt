package com.serg.arcab.ui.main

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.serg.arcab.R
import com.serg.arcab.base.BaseActivity
import org.koin.android.architecture.ext.viewModel

class MainActivity : BaseActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            replaceFragment(GetStartedFragment.newInstance(), GetStartedFragment.TAG)
        }

        viewModel.backAction.observe(this, Observer {
            popFragment()
        })

        viewModel.goToLinkId.observe(this, Observer {
            addFragment(LinkIdFragment.newInstance(), LinkIdFragment.TAG)
        })

        viewModel.goToPlaces.observe(this, Observer {
            addFragment(PlacesFragment.newInstance(), PlacesFragment.TAG)
        })

        viewModel.goToPickupPoint.observe(this, Observer {
            addFragment(PickupPointFragment.newInstance(), PlacesFragment.TAG)
        })

        viewModel.goToPickupTiming.observe(this, Observer {
            addFragment(PickupTimingFragment.newInstance(), PlacesFragment.TAG)
        })

    }

    private fun popFragment() {
        supportFragmentManager.popBackStack()
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit()
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, tag)
                .addToBackStack("my_stack")
                .commit()
    }

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, MainActivity::class.java))
        }
    }
}
