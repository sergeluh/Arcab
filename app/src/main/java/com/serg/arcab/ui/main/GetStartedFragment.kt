package com.serg.arcab.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseAuth
import com.serg.arcab.LocationManager
import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.model.TripOrder
import com.serg.arcab.ui.main.dialogs.AchievementFragment
import com.serg.arcab.ui.main.dialogs.AlertFragment
import com.serg.arcab.ui.main.dialogs.UpNextFragment
import com.serg.arcab.ui.splash.SplashActivity
import kotlinx.android.synthetic.main.fragment_get_started.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel
import org.koin.android.ext.android.inject
import timber.log.Timber

class GetStartedFragment : BaseFragment(), OnMapReadyCallback {

    private val viewModel by sharedViewModel<MainViewModel>()

    private val locationManager by inject<LocationManager>()

    private var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_get_started, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("onActivityCreated")
        val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        navBar.nextBtn.setOnClickListener {
            val fragment = AchievementFragment()
            fragment.dismissListener = object : AchievementFragment.DismissListener {
                override fun onDismess() {
                    val upNextFragment = UpNextFragment()
                    upNextFragment.setFields("Rate your ride.", "How was your trip? Rate the driver and give us your feedback.")
                    upNextFragment.dismissListener = object : UpNextFragment.DismissListener {
                        override fun onDismess() {
                            val alert = AlertFragment()
                            alert.setHeaderAndMessage("Oops, your balance is low.", "Looks like you`re running low! You should top up your account to continue using arcab.")
                            alert.dismissListener = object : AlertFragment.DismissListener {
                                override fun onDismess() {
                                    viewModel.onGoToLinkIdClicked()
                                }
                            }
                            alert.show(childFragmentManager, AlertFragment.TAG)
                        }
                    }
                    upNextFragment.show(childFragmentManager, UpNextFragment.TAG)
                }
            }
            fragment.setFields("Hatttrick",
                    R.drawable.ic_confetti,
                    "You`ve taken an arcab 3 days in a row.",
                    3,
                    R.drawable.ic_cup,
                    "You`ve earnt a badge as an honor from the arcab family. We appreciate you!")
            fragment.show(childFragmentManager, AchievementFragment.TAG)
        }

        navBar.backBtn.visibility = View.GONE

        signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context, SplashActivity::class.java))
            activity!!.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        //Reset viewModel data
        viewModel.tripsTo = mutableListOf()
        viewModel.tripsFrom = mutableListOf()
        viewModel.commonPoints = null
        viewModel.university = null
        viewModel.tripOrder = TripOrder()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Timber.d("onMapReady ${googleMap.hashCode()}")
        val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_arcab))
        if (success) {
            Timber.d("Styles applied")
        } else {
            Timber.d("Styles are broken")
        }
        this.googleMap = googleMap
        if (checkLocationPermissions()) {
            onPermissionLocationGranted()
        } else {
            requestLocationPermissions(RC_LOCATION_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RC_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionLocationGranted()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun onPermissionLocationGranted() {
        Timber.d("onPermissionLocationGranted ${googleMap?.hashCode()}")
        googleMap?.isMyLocationEnabled = true

        locationManager.requestLastLocation(object : LocationManager.LastLocationCallback() {
            override fun onSuccess(location: Location?) {
                location?.also {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 16f))
                }
            }
        })
    }

    companion object {

        const val TAG = "GetStartedFragment"
        const val RC_LOCATION_PERMISSION = 0

        @JvmStatic
        fun newInstance() = GetStartedFragment()
    }
}
