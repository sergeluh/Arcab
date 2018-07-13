package com.serg.arcab.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import com.serg.arcab.R
import org.koin.android.architecture.ext.sharedViewModel
import com.serg.arcab.model.Seat
import kotlinx.android.synthetic.main.fragment_preferred_seat.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import timber.log.Timber


class PreferredSeatFragment : Fragment(), PreferredSeatRecyclerViewAdapter.Callback {

    private var dayIndex: Int? = null

    override fun checkedChange(seat: Seat?) {
        if(seat == null) {
            navBar.nextBtn.isEnabled = false
            text_view_seat.visibility = View.INVISIBLE
        } else {
            navBar.nextBtn.isEnabled = true
            text_view_seat.text = seat.id
            text_view_seat.visibility = View.VISIBLE
            //Set user selected seats when new seats selected
            with(viewModel.tripOrder){
                preferredSeat = seat
                preferredSeat!!.user_id = FirebaseAuth.getInstance().uid
                preferredSeat!!.user_point = userPoint
            }
        }
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preferred_seat, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
            //Remove user preferred seat data
            viewModel.tripOrder.preferredSeat = null
        }

        navBar.nextBtn.setOnClickListener {
            viewModel.tripOrder.dayIndex = dayIndex
            viewModel.onGoToPaymentPlanClicked()
            Timber.d("Preferred seats selected: ${viewModel.tripOrder}")
        }

        navBar.nextBtn.isEnabled = false

        with(viewModel.tripOrder.pickMeUpAt?.daysChecked) {
            for (index in 0 until this!!.size) {
                if (this[index]){
                    dayIndex = index + 1
                    break
                }
            }
        }
        Timber.d("Day index is $dayIndex")
        var reservedSeats = mutableListOf<String?>()
                viewModel.tripsTo.filter { it.id ==  viewModel.tripOrder.pickMeUpAt?.tripId}[0]
                .booked_days?.get(dayIndex!!)?.seats?.forEach { reservedSeats.add(it.value.id) }

        var seats = mutableListOf<Seat>()
        val seatCodes = arrayOf("D", "C", "B", "A")
        for (i in 4 downTo 1) {
            for (code in seatCodes.iterator()) {
                var seatId = "$i$code"

                if(reservedSeats.contains(seatId)){
                    Timber.d("Seat $seatId is reserved")
                    seats.add(Seat(seatId, "sdf"))//одно место занято
                } else {
                    //Add flags to seats items to detect whether the user select current seats
                    seats.add(Seat(seatId))
                }
            }
        }

        val seatAdapter = PreferredSeatRecyclerViewAdapter(this, seats,
                viewModel.tripOrder.preferredSeat?.id)

        preferred_seat_recycler_view.layoutManager = GridLayoutManager(context, seatCodes.size)
        preferred_seat_recycler_view.adapter = seatAdapter

    }

    override fun onResume() {
        super.onResume()
        //Show text_view_seat and populate it with user seats data when user comes back from on of the other fragments
        if (viewModel.tripOrder.preferredSeat != null){
            text_view_seat.visibility = View.VISIBLE
            text_view_seat.text = viewModel.tripOrder.preferredSeat?.id
            navBar.nextBtn.isEnabled = true
        }
    }

    companion object {

        const val TAG = "PickupPointFragment"

        @JvmStatic
        fun newInstance() = PreferredSeatFragment()
    }
}
