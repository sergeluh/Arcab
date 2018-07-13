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
import com.serg.arcab.model.TimingItem
import com.serg.arcab.model.Trip
import timber.log.Timber


class PickupTimingFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pickup_timing, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
        }

        navBar.nextBtn.setOnClickListener {
            viewModel.onGoToPreferredSeatClicked()
            viewModel.tripOrder.resultMessage = when{
                check_box_common_point.isChecked && check_box_your_point.isChecked -> resources.getString(R.string.initial_setup_result_message_to_from)
                check_box_common_point.isChecked -> resources.getString(R.string.initial_setup_result_message_to_only)
                check_box_your_point.isChecked -> resources.getString(R.string.initial_setup_result_message_from_only)
                else -> ""
            }
            //Set viewModel tripOrder timing items
            setTimingItem(common_recycler_view, your_recycler_view)
            Timber.d("Trip order at now is: ${viewModel.tripOrder}")
        }

        navBar.nextBtn.isEnabled = false

        check_box_common_point.setOnClickListener {
            setVisibility(common_recycler_view, check_box_common_point.isChecked)
            checkSelected()
        }

        check_box_your_point.setOnClickListener {
            setVisibility(your_recycler_view, check_box_your_point.isChecked)
            checkSelected()
        }

        //Initialize data for pickMeUp recycler
        val recyclerDataPickMe = getTimingItemList(viewModel.tripsTo)

        var commonAdapter = TimingRecyclerViewAdapter(recyclerDataPickMe, common_recycler_view)

        common_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        common_recycler_view.adapter = commonAdapter

        val commonSnapHelper = PagerSnapHelper()
        commonSnapHelper.attachToRecyclerView(common_recycler_view)

        textViewCommonPointAvailable.text = String.format(getString(R.string.initial_setup_pickup_timing_available), recyclerDataPickMe.size)

        //Initialize data for dropMeOff recycler
        val recyclerDataDropMe = getTimingItemList(viewModel.tripsFrom)

        var yourAdapter = TimingRecyclerViewAdapter(recyclerDataDropMe, your_recycler_view)

        your_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
        your_recycler_view.adapter = yourAdapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(your_recycler_view)

        textViewYourPointAvailable.text = String.format(getString(R.string.initial_setup_pickup_point_available), recyclerDataDropMe.size)

    }

    override fun onResume() {
        super.onResume()
        //Set init visibility if user comes back from one of the next fragments
        setInitialVisibility()
        setTimingItemFromTripOrder(common_recycler_view,
                (common_recycler_view.adapter as TimingRecyclerViewAdapter).getItems(),
                viewModel.tripOrder.pickMeUpAt, viewModel.tripOrder.pickMeUpAtIndex)
        setTimingItemFromTripOrder(your_recycler_view,
                (your_recycler_view.adapter as TimingRecyclerViewAdapter).getItems(),
                viewModel.tripOrder.dropMeOffAt, viewModel.tripOrder.dropMeOfAtIndex)
        //Set user preferred seat to null
        viewModel.tripOrder.preferredSeat = null
    }

    private fun checkSelected() {
        navBar.nextBtn.isEnabled = check_box_common_point.isChecked || check_box_your_point.isChecked
    }

    private fun setVisibility(view: RecyclerView, isVisible: Boolean = false) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    //Method for setting initial visibility of the recycler view items and setting "Next" button enabled if it needs to
    private fun setInitialVisibility() {
        setVisibility(common_recycler_view, check_box_common_point.isChecked)
        setVisibility(your_recycler_view, check_box_your_point.isChecked)
        checkSelected()
    }

    //Method for adding timing item
    private fun setTimingItem(pickupView: RecyclerView, dropOffView: RecyclerView) {
        if (check_box_common_point.isChecked) {
            val index = (pickupView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val pickup = (pickupView.adapter as TimingRecyclerViewAdapter).getItem(index)
            viewModel.tripOrder.pickMeUpAt = if (pickup.daysEnabled.contains(true)) pickup else null
            viewModel.tripOrder.pickMeUpAtIndex = index
        }else{
            viewModel.tripOrder.pickMeUpAt = null
        }
        if (check_box_your_point.isChecked) {
            val index = (dropOffView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val dropOff = (dropOffView.adapter as TimingRecyclerViewAdapter).getItem(index)
            viewModel.tripOrder.dropMeOffAt = if (dropOff.daysEnabled.contains(true)) dropOff else null
            viewModel.tripOrder.dropMeOfAtIndex = index
        }else{
            viewModel.tripOrder.dropMeOffAt = null
        }
    }

    //Method for set visible timing item to selected
    private fun setTimingItemFromTripOrder(view: RecyclerView, initialList: MutableList<TimingItem>, item: TimingItem?, index: Int?){
        if (view.visibility == View.VISIBLE){
            initialList[index!!] = item!!
            view.adapter.notifyDataSetChanged()
        }
    }

    //Method for converting trip list to timing item list for adapters
    private fun getTimingItemList(trips: MutableList<Trip>): MutableList<TimingItem>{
        val resultList = mutableListOf<TimingItem>()
        trips.forEach {
            var checkedList = mutableListOf<Boolean>(false, false, false, false, false, false, false)
            it.booked_days?.filter { it != null }?.forEach {
                checkedList[it.index!!-1] = true
            }
            resultList.add(TimingItem(it.time!!, checkedList, tripId = it.id))
        }
        return resultList
    }

    companion object {

        const val TAG = "PickupPointFragment"

        @JvmStatic
        fun newInstance() = PickupTimingFragment()
    }
}
