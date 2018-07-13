package com.serg.arcab.ui.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions

import com.serg.arcab.R
import com.serg.arcab.base.BaseFragment
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

        navBar.nextBtn.text = "Confirm"

        val map = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        map.onCreate(null)
        map.getMapAsync {
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(viewModel.tripOrder.currentLocation, 16f))
            it.addMarker(MarkerOptions().position(viewModel.tripOrder.currentLocation!!))
        }

        header_text.text = viewModel.tripOrder.resultMessage

        pickup_text.text = viewModel.tripOrder.pickupMessage

        address.text = "From ${viewModel.tripOrder.address}"

        navBar.nextBtn.setOnClickListener{
            viewModel.onConfirmOrderClicked()
        }

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }
    }

    companion object {
        fun newInstance() = ResultFragment()
    }
}
