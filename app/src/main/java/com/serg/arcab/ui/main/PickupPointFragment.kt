package com.serg.arcab.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.serg.arcab.R
import kotlinx.android.synthetic.main.fragment_pickup_point.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel
import android.support.v7.widget.PagerSnapHelper
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.serg.arcab.LocationManager
import com.serg.arcab.model.UserPoint
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*


class PickupPointFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private val locationManager by inject<LocationManager>()
    private var googleMap: GoogleMap? = null
    private var geocoder: Geocoder? = null
    private var address: String? = null
    private var fromLatLng: LatLng? = null
    private var addressView: TextView? = null
    private val requestLocationPermissions = 1002

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pickup_point, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addressView = your_address

        //Initialize map view for picking custom location
        val mapFragment = childFragmentManager.findFragmentById(R.id.your_map_view) as SupportMapFragment
        mapFragment.onCreate(null)
        mapFragment.getMapAsync {
            it.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_arcab))
            googleMap = it
            googleMap?.setOnMapClickListener {
                googleMap?.clear()
                googleMap?.addMarker(MarkerOptions().position(it))
                address = geocoder!!.getFromLocation(it.latitude, it.longitude, 1)[0]
                        .getAddressLine(0)
                addressView!!.text = "From $address"
            }
            viewModel.tripOrder.currentLocation?.also {
                googleMap?.addMarker(MarkerOptions().position(it))
                setAddressAndPosition(it.latitude, it.longitude)
            }
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), requestLocationPermissions)
        }

        geocoder = Geocoder(context, Locale.getDefault())

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        navBar.nextBtn.setOnClickListener {
            viewModel.onGoToPickupTimingClicked()
            //Add pickup point by clicking nextBtn
            if (check_box_common_point.isChecked) {
                val visibleItemIndex = (common_recycler_view.layoutManager as LinearLayoutManager)
                        .findFirstVisibleItemPosition()
                val commonPoint = viewModel.commonPoints!![visibleItemIndex]
                viewModel.tripOrder.currentLocation = LatLng(commonPoint.latitude!!, commonPoint.longitude!!)
                viewModel.tripOrder.userPoint = UserPoint(commonPoint.id, type = "common_point")
                viewModel.tripOrder.pickupMessage = ""
                viewModel.tripOrder.address = commonPoint.address
            } else {
                viewModel.tripOrder.address = address
                viewModel.tripOrder.currentLocation = fromLatLng
                viewModel.tripOrder.userPoint = UserPoint(null, address, fromLatLng?.latitude,
                        fromLatLng?.longitude, type = "custom")
                viewModel.tripOrder.pickupMessage = resources.getString(R.string.initial_setup_result_premium_pickup)
            }
            Timber.d("Current order is ${viewModel.tripOrder}")
        }

        navBar.nextBtn.isEnabled = false

        check_box_common_point.setOnClickListener(View.OnClickListener {
            setVisibility(common_recycler_view, check_box_common_point.isChecked)
            if (check_box_your_point.isChecked) {
                check_box_your_point.isChecked = false
                setVisibility(your_container)
            }
            checkSelected()
        })

        check_box_your_point.setOnClickListener(View.OnClickListener {
            setVisibility(your_container, check_box_your_point.isChecked)
            if (check_box_common_point.isChecked) {
                check_box_common_point.isChecked = false
                setVisibility(common_recycler_view)
            }
            checkSelected()
        })

        textViewCommonPointAvailable.text = String.format(
                getString(R.string.initial_setup_pickup_point_available), viewModel.commonPoints?.size)


        val commonAdapter = PointRecyclerViewAdapter(viewModel.commonPoints!!, common_recycler_view)

        common_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        common_recycler_view.adapter = commonAdapter

        val commonSnapHelper = PagerSnapHelper()
        commonSnapHelper.attachToRecyclerView(common_recycler_view)

        textViewYourPointAvailable.text = String.format(getString(R.string.initial_setup_pickup_point_available), 1)
    }

    override fun onResume() {
        super.onResume()
        //Set init visibility if user comes back from one of the next fragments
        setInitialVisibility()

        //Reset trip order timing items when user returns from one of the next fragments
        with(viewModel.tripOrder) {
            pickMeUpAt = null
            pickMeUpAtIndex = null
            dropMeOffAt = null
            dropMeOfAtIndex = null
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestLocationPermissions -> {
                if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    googleMap?.isMyLocationEnabled = true
                    if (address == null) {
                        locationManager.requestLastLocation(object : LocationManager.LastLocationCallback() {
                            override fun onSuccess(location: Location?) {
                                super.onSuccess(location)
                                location?.also {
                                    setAddressAndPosition(it.latitude, it.longitude)
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    private fun checkSelected() {
        navBar.nextBtn.isEnabled = check_box_common_point.isChecked || check_box_your_point.isChecked
    }

    private fun setVisibility(view: View, isVisible: Boolean = false) {
        if (isVisible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }

    }

    //Method that sets address and user position on the map
    private fun setAddressAndPosition(latitude: Double, longitude: Double){
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16f))
        val myAddress = geocoder!!
                .getFromLocation(latitude, longitude,
                        1)[0]
        address = myAddress.getAddressLine(0)
        addressView?.text = "From $address"
        fromLatLng = LatLng(latitude, longitude)
    }

    //Method for setting initial visibility of the recycler view items and setting "Next" button enabled if it needs to
    private fun setInitialVisibility() {
        setVisibility(common_recycler_view, check_box_common_point.isChecked)
        setVisibility(your_container, check_box_your_point.isChecked)
        checkSelected()
    }

    companion object {

        const val TAG = "PickupPointFragment"

        @JvmStatic
        fun newInstance() = PickupPointFragment()
    }
}
