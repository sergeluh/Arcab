package com.serg.arcab.ui.main

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.serg.arcab.*
import com.serg.arcab.base.BaseActivity
import com.serg.arcab.model.Seat
import com.serg.arcab.model.UserPoint
import com.serg.arcab.ui.auth.CaptureFragment
import com.serg.arcab.utils.ResultFragment
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

        viewModel.goToPreferredSeat.observe(this, Observer {
            addFragment(PreferredSeatFragment.newInstance(), PlacesFragment.TAG)
        })

        viewModel.goToPaymentPlan.observe(this, Observer {
            addFragment(PaymentPlanFragment.newInstance(), PlacesFragment.TAG)
        })

        viewModel.goToResult.observe(this, Observer {
            addFragment(ResultFragment.newInstance(), PlacesFragment.TAG)
        })

        viewModel.goToScan.observe(this, Observer {
            addFragment(CaptureFragment.newInstance(), CaptureFragment.TAG)
        })

        viewModel.goToLocationOnMap.observe(this, Observer {
            addFragment(LocationOnMapFragment.newInstance(), LocationOnMapFragment.TAG)
        })

        viewModel.confirmOrder.observe(this, Observer {
            val uid = FirebaseAuth.getInstance().uid
            with(viewModel.tripOrder){
                resultSeatsTo?.forEach {
                    writeSeatToDb(tripIdTo!!, it.key, it.value, uid!!, userPoint!!)
                }
                resultSeatsFrom?.forEach {
                    writeSeatToDb(tripIdFrom!!, it.key, it.value, uid!!, userPoint!!)
                }
            }
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            addFragment(GetStartedFragment.newInstance(), PlacesFragment.TAG)
        })

        viewModel.goToNotAvailableFragment.observe(this, Observer {
            addFragment(UniversityNotAvailableFragment.newInstance(), PlacesFragment.TAG)
        })

        viewModel.letMeIn.observe(this, Observer {
            val messageText = if (it != null && it.isNotEmpty()) "Your organization ($it) will be notified"
            else "Enter organization name please"
            Toast.makeText(this, messageText, Toast.LENGTH_SHORT).show()
        })

        viewModel.hideKeyboard.observe(this, Observer {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
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
                .setCustomAnimations(R.anim.show_fragment, R.anim.hide_fragment,
                        R.anim.pop_enter_fragment, R.anim.pop_exit_fragment)
                .replace(R.id.container, fragment, tag)
                .addToBackStack("my_stack")
                .commit()
    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }

    //Method wor writing preferred user seat to db in selected days
    private fun writeSeatToDb(tripId: Int, dayIndex: Int, seatId: String, uid: String, userPoint: UserPoint){
        FirebaseDatabase.getInstance().reference.child(TRIPS_FIREBASE_TABLE)
                .child(tripId.toString())
                .child(BOOKED_DAYS_FIREBASE_TABLE)
                .child(dayIndex.toString())
                .child(SEATS_FIREBASE_TABLE)
                .child(seatId)
                .setValue(Seat(seatId, uid, userPoint))
    }
}
