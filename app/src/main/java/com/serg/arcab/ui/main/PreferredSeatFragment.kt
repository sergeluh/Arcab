package com.serg.arcab.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.serg.arcab.R
import org.koin.android.architecture.ext.sharedViewModel
import com.serg.arcab.model.Seat
import com.serg.arcab.model.UserPoint
import kotlinx.android.synthetic.main.fragment_preferred_seat.*
import kotlinx.android.synthetic.main.navigation_view.view.*


class PreferredSeatFragment : Fragment(), PreferredSeatRecyclerViewAdapter.Callback {

    override fun checkedChange(seat: Seat?) {
        if(seat == null) {
            navBar.nextBtn.isEnabled = false
            text_view_seat.visibility = View.INVISIBLE
        } else {
            navBar.nextBtn.isEnabled = true
            text_view_seat.text = seat.id
            text_view_seat.visibility = View.VISIBLE
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
        }

        navBar.nextBtn.setOnClickListener {
            viewModel.onGoToPaymentPlanClicked()
        }

        navBar.nextBtn.isEnabled = false


        var seats = mutableListOf<Seat>()
        val seatCodes = arrayOf("D", "C", "B", "A")
        for (i in 4 downTo 1) {
            for (code in seatCodes.iterator()) {
                var seatId = "$i$code"

                if(seatId == "1A"){
                    seats.add(Seat(seatId, "sdf"))//одно место занято
                } else {
                    seats.add(Seat(seatId))
                }
            }
        }


        var seatAdapter = PreferredSeatRecyclerViewAdapter(this, seats)

        preferred_seat_recycler_view.layoutManager = GridLayoutManager(context, seatCodes.size)
        preferred_seat_recycler_view.adapter = seatAdapter

    }


    companion object {

        const val TAG = "PickupPointFragment"

        @JvmStatic
        fun newInstance() = PreferredSeatFragment()
    }
}
