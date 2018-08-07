package com.serg.arcab.utils


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
import com.serg.arcab.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_result.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel

class ResultFragment : BaseFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.nextBtn.text = resources.getString(R.string.button_confirm)

        val map = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        map.onCreate(null)
        map.getMapAsync {
            it.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_arcab))
            val gm = it
            viewModel.tripOrder.currentLocation?.also {
                gm.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 16f))
                gm.addMarker(MarkerOptions().position(it))
            }
        }

        header_text.text = viewModel.tripOrder.resultMessage

        pickup_text.text = viewModel.tripOrder.pickupMessage

        address.text = String.format(resources.getString(R.string.initial_setup_pickup_point_from),
                viewModel.tripOrder.address)

        navBar.nextBtn.setOnClickListener{
            viewModel.onConfirmOrderClicked()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ResultFragment()
    }
}
