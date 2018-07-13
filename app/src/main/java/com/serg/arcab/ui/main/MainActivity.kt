package com.serg.arcab.ui.main

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.serg.arcab.R
import com.serg.arcab.base.BaseActivity
import org.koin.android.architecture.ext.viewModel
import timber.log.Timber

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

        viewModel.goToPreferredSeat.observe(this, Observer {
            addFragment(PreferredSeatFragment.newInstance(), PlacesFragment.TAG)
        })

        viewModel.goToPaymentPlan.observe(this, Observer {
            addFragment(PaymentPlanFragment.newInstance(), PlacesFragment.TAG)
        })

        viewModel.goToResult.observe(this, Observer {
            addFragment(ResultFragment.newInstance(), PlacesFragment.TAG)
        })

        viewModel.confirmOrder.observe(this, Observer {
            FirebaseDatabase.getInstance().reference.child("trips")
                    .child(viewModel.tripOrder.pickMeUpAt?.tripId.toString())
                    .child("booked_days")
                    .child(viewModel.tripOrder.dayIndex.toString())
                    .child("seats")
                    .child(viewModel.tripOrder.preferredSeat?.id!!)
                    .setValue(viewModel.tripOrder.preferredSeat)
        })

        viewModel.goToNotAvailableFragment.observe(this, Observer {
            addFragment(UniversityNotAvailableFragment.newInstance(), PlacesFragment.TAG)
        })

        viewModel.letMeIn.observe(this, Observer {
            Timber.d("Observer received string: $it")
            val messageText = if (it != null && it.isNotEmpty()) "Your organization ($it) will be notified"
            else "Enter organization name please"
            Toast.makeText(this, messageText, Toast.LENGTH_SHORT).show()
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
