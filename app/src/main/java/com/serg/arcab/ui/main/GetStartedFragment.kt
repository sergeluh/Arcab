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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.serg.arcab.LocationManager
import com.serg.arcab.R
import com.serg.arcab.USERS_FIREBASE_TABLE
import com.serg.arcab.User
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.model.TripOrder
import com.serg.arcab.ui.main.dialogs.UtilFragment
import com.serg.arcab.ui.splash.SplashActivity
import kotlinx.android.synthetic.main.fragment_get_started.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

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


        FirebaseAuth.getInstance().currentUser?.providers?.forEach {
            Timber.d("MYPROVIDER $it")
        }

        navBar.nextBtn.setOnClickListener {
//            val fragment = AchievementFragment()
//            fragment.dismissListener = object : AchievementFragment.DismissListener {
//                override fun onDismess() {
//                    val upNextFragment = UpNextFragment()
//                    upNextFragment.setFields("Rate your ride.", "How was your trip? Rate the driver and give us your feedback.")
//                    upNextFragment.dismissListener = object : UpNextFragment.DismissListener {
//                        override fun onDismiss() {
//                            val alert = AlertFragment()
//                            alert.setHeaderAndMessage("Oops, your balance is low.", "Looks like you`re running low! You should top up your account to continue using arcab.")
//                            alert.dismissListener = object : AlertFragment.DismissListener {
//                                override fun onDismess() {
//                                    viewModel.onGoToLinkIdClicked()
//                                }
//                            }
//                            alert.show(childFragmentManager, AlertFragment.TAG)
//                        }
//                    }
//                    upNextFragment.show(childFragmentManager, UpNextFragment.TAG)
//                }
//            }
//            fragment.setFields("Hatttrick",
//                    R.drawable.ic_confetti,
//                    "You`ve taken an arcab 3 days in a row.",
//                    3,
//                    R.drawable.ic_cup,
//                    "You`ve earnt a badge as an honor from the arcab family. We appreciate you!")
//            fragment.show(childFragmentManager, AchievementFragment.TAG)
            viewModel.onGoToLinkIdClicked()
        }

        navBar.backBtn.visibility = View.GONE

//        if (!viewModel.wasAppOpened()){
            FirebaseAuth.getInstance().currentUser?.also {

                checkReferFriend(it.metadata?.creationTimestamp)
                checkVerification(it.metadata?.creationTimestamp)

                FirebaseDatabase.getInstance().reference.child(USERS_FIREBASE_TABLE).child(it.uid).child("first_name").addListenerForSingleValueEvent( object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val name = p0.value as String
                        val alert = UtilFragment()
                        alert.header = "Welcome to arcab"
                        alert.message = "Hi $name, tap to begin setting up your daily ride."
                        alert.buttonText = "Get started"
                        alert.imageResource = R.drawable.placeholder
                        alert.callback = object : UtilFragment.Callback{
                            override fun alertButtonClicked() {
                                alert.dismiss()
                            }

                            override fun onDismiss() {
                                checkReschedule()
                            }
                        }

                        if(!isDetached) {
                            alert.show(childFragmentManager, UtilFragment.TAG)
                        }

                        viewModel.setAppOpened(true)
                    }
                })
            }


//        }else{
//            checkReschedule()
//        }

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

    //Show alert at the begining of the week if there is need to reschedule user timings
    private fun checkReschedule(){
        if (viewModel.getReschedule()) {
            val calendar = Calendar.getInstance()
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                val alert = UtilFragment()
                alert.header = "Update your schedule for next week."
                alert.message = "Just a remainder, to choose your set preferences for upcoming week."
                alert.buttonText = "Update schedule"
                alert.callback = object : UtilFragment.Callback{
                    override fun alertButtonClicked() {
                        alert.dismiss()
                    }

                    override fun onDismiss() {

                    }
                }

                alert.show(childFragmentManager, UtilFragment.TAG)
            }
        }
    }

    //Shows if user haven't verified his ID and there is less than 7 days passed from registration
    private fun checkVerification(timestamp: Long?){
        timestamp?.also {
            val convertToDays = 1000 * 60 * 60 * 24
            val currentTime = System.currentTimeMillis()
            val difference = (currentTime - timestamp) / convertToDays
//            if (difference < 7){
                val alert = UtilFragment()
                alert.header = "Verify your ID"
                alert.message = "Scan your Emirates ID within the next ${7 - difference} days to continue using arcab"
                alert.buttonText = "Scan now"
                alert.imageResource = R.drawable.placeholder
                alert.callback = object : UtilFragment.Callback{
                    override fun alertButtonClicked() {
                        alert.dismiss()
                        viewModel.onGoToScanClicked()
                    }

                    override fun onDismiss() {

                    }
                }

                alert.show(childFragmentManager, UtilFragment.TAG)
//            }
        }
    }

    //Shows only if user haven't refers his friend to arcab and there is less than 7 days passed from registration
    private fun checkReferFriend(timestamp: Long?){
        timestamp?.also {
            val convertToDays = 1000 * 60 * 60 * 24
            val currentTime = System.currentTimeMillis()
            val difference = (currentTime - timestamp) / convertToDays
//            if (difference < 7){
            val alert = UtilFragment()
            alert.header = "Free ride's on us!"
            alert.message = "get your free ride when you refer a friend an they sign up for arcab."
            alert.buttonText = "Refer a friend"
            alert.imageResource = R.drawable.placeholder
            alert.callback = object : UtilFragment.Callback{
                override fun alertButtonClicked() {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, "Follow this link to join the arcab: https://www.google.com/")
                    startActivity(intent)
                    alert.dismiss()
                }

                override fun onDismiss() {

                }
            }

            alert.show(childFragmentManager, UtilFragment.TAG)
//            }
        }
    }

    companion object {

        const val TAG = "GetStartedFragment"
        const val RC_LOCATION_PERMISSION = 0

        @JvmStatic
        fun newInstance() = GetStartedFragment()
    }
}
