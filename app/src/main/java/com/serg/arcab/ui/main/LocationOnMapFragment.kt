package com.serg.arcab.ui.main

import android.app.Activity
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentActivity
import android.widget.TextView
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.serg.arcab.LocationManager

import com.serg.arcab.R
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class LocationOnMapFragment : FragmentActivity(){

    companion object {
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
    }

    private var latitude: Double? = null
    private var longitude: Double? = null
    private var googleMap: GoogleMap? = null
    private var geocoder: Geocoder? = null
    private val locationManager by inject<LocationManager>()
    private var addressView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_location_on_map)

        val navBar = findViewById<ConstraintLayout>(R.id.navBar)
        val nextButton = navBar.findViewById<TextView>(R.id.nextBtn)
        nextButton.text = resources.getString(R.string.button_continue)
        nextButton.setOnClickListener {
            val resultIntent = intent
            resultIntent.putExtra(LATITUDE, latitude)
            resultIntent.putExtra(LONGITUDE, longitude)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        navBar.backBtn.setOnClickListener {
            finish()
        }
        addressView = findViewById(R.id.address)

        geocoder = Geocoder(this, Locale.getDefault())

        val locationOnMap = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        locationOnMap.getMapAsync{
            it.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_arcab))
            it.setOnCameraIdleListener {
                val latLng = it.cameraPosition.target
                latitude = latLng.latitude
                longitude = latLng.longitude
                addressView?.text = " ${geocoder!!.getFromLocation(latLng.latitude, latLng.longitude, 1)[0].getAddressLine(0)}"
            }
            googleMap = it
        }
        locationManager.requestLastLocation(object : LocationManager.LastLocationCallback() {
            override fun onSuccess(location: Location?) {
                super.onSuccess(location)
                location?.also {
                    latitude = it.latitude
                    longitude = it.longitude
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude) , 16f))
                    addressView?.text = " ${geocoder!!.getFromLocation(it.latitude, it.longitude, 1)[0].getAddressLine(0)}"
                }
            }
        })
    }
}
