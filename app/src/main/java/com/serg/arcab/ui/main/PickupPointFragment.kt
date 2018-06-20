package com.serg.arcab.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.serg.arcab.R
import kotlinx.android.synthetic.main.fragment_pickup_point.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import org.koin.android.architecture.ext.sharedViewModel
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.SnapHelper



class PickupPointFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pickup_point, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        navBar.nextBtn.setOnClickListener {
            viewModel.onGoToPickupTimingClicked()
        }

        navBar.nextBtn.isEnabled = false

        check_box_common_point.setOnClickListener(View.OnClickListener {
            setVisibility(common_recycler_view, check_box_common_point.isChecked)
            if(check_box_your_point.isChecked) {
                check_box_your_point.isChecked = false
                setVisibility(your_recycler_view)
            }
            checkSelected()
        })

        check_box_your_point.setOnClickListener(View.OnClickListener {
            setVisibility(your_recycler_view, check_box_your_point.isChecked)
            if(check_box_common_point.isChecked) {
                check_box_common_point.isChecked = false
                setVisibility(common_recycler_view)
            }
            checkSelected()
        })

        //
        var commonData = mutableListOf<String>()
        commonData.add("First point")
        commonData.add("First point")


        var commonAdapter = PointRecyclerViewAdapter(commonData, common_recycler_view)

        common_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        common_recycler_view.adapter = commonAdapter

        val commonSnapHelper = PagerSnapHelper()
        commonSnapHelper.attachToRecyclerView(common_recycler_view)

        textViewCommonPointAvailable.text = String.format(getString(R.string.initial_setup_pickup_point_available), commonData.size)

        //

        var yourData = mutableListOf<String>()
        yourData.add("My First point")
        yourData.add("My asdfdfs point")
        yourData.add("My asdfdfs point")


        var yourAdapter = PointRecyclerViewAdapter(yourData, your_recycler_view)

        your_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        your_recycler_view.adapter = yourAdapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(your_recycler_view)

        textViewYourPointAvailable.text = String.format(getString(R.string.initial_setup_pickup_point_available), yourData.size)

        /*mapView.getMapAsync {

        }
        mapView.onCreate(savedInstanceState)

        mapView2.getMapAsync {

        }
        mapView2.onCreate(savedInstanceState)*/
    }

    private fun checkSelected() {
        navBar.nextBtn.isEnabled = check_box_common_point.isChecked || check_box_your_point.isChecked
    }

    private fun setVisibility(view: RecyclerView, isVisible: Boolean = false) {
        if(isVisible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }

    }

    override fun onStop() {
        super.onStop()
        /*mapView.onStop()
        mapView2.onStop()*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        /*mapView.onDestroy()
        mapView2.onDestroy()*/
    }

    override fun onResume() {
        super.onResume()
        /*mapView.onResume()
        mapView2.onResume()*/
    }

    override fun onPause() {
        super.onPause()
        /*mapView.onPause()
        mapView2.onPause()*/
    }

    override fun onLowMemory() {
        super.onLowMemory()
        /*mapView.onLowMemory()
        mapView2.onLowMemory()*/
    }

    companion object {

        const val TAG = "PickupPointFragment"

        @JvmStatic
        fun newInstance() = PickupPointFragment()
    }
}
