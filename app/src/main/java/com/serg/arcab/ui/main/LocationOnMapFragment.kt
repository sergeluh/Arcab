package com.serg.arcab.ui.main

import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.serg.arcab.COMMON_POINTS_FIREBASE_TABLE
import com.serg.arcab.LocationManager

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.model.CommonPoint
import kotlinx.android.synthetic.main.fragment_location_on_map.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class LocationOnMapFragment : BaseFragment(){

    private var googleMap: GoogleMap? = null
    private var geocoder: Geocoder? = null
    private val locationManager by inject<LocationManager>()
    private val viewModel by sharedViewModel<MainViewModel>()
    private var firstDetection = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location_on_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.text = resources.getString(R.string.button_continue)
        navBar.nextBtn.isEnabled = false
        navBar.nextBtn.setOnClickListener {
            showLoading()
            viewModel.commonPoints = mutableListOf()
            FirebaseDatabase.getInstance().reference.child(COMMON_POINTS_FIREBASE_TABLE)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            hideLoading()
                        }
                        override fun onDataChange(p0: DataSnapshot) {
                            for (snapshot in p0.children) {
                                viewModel.commonPoints?.add(snapshot.getValue(CommonPoint::class.java)!!)
                            }
                            hideLoading()
                            viewModel.onGoToPickupPointClicked()
                        }

                    })
        }

        address?.text = resources.getString(R.string.address_text)

        navBar.backBtn.setOnClickListener {
           viewModel.onBackClicked()
        }

        geocoder = Geocoder(context, Locale.getDefault())

        val locationOnMap = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        locationOnMap.getMapAsync{
            it.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_arcab))
            it.setOnCameraIdleListener {
                if (!firstDetection) {
                    val latLng = it.cameraPosition.target
                    viewModel.tripOrder.currentLocation = LatLng(latLng.latitude, latLng.longitude)
                    Timber.d("RECEIVEDLOCATION ${viewModel.tripOrder.currentLocation?.latitude} : ${viewModel.tripOrder.currentLocation?.longitude}")
                    if (!navBar.nextBtn.isEnabled) {
                        navBar.nextBtn.isEnabled = viewModel.tripOrder.currentLocation != null
                    }
                    val addressText = " ${geocoder!!.getFromLocation(latLng.latitude, latLng.longitude, 1)[0].getAddressLine(0)}"
                    Timber.d("RECEIVEDLOCATION $address")
                    address.text = addressText
                }
                firstDetection = false
            }
            googleMap = it
        }
        locationManager.requestLastLocation(object : LocationManager.LastLocationCallback() {
            override fun onSuccess(location: Location?) {
                super.onSuccess(location)
                location?.also {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude) , 16f))
                }
            }
        })
    }

    companion object {
        const val TAG = "locationOnMao"
        fun newInstance() = LocationOnMapFragment()
    }
}
