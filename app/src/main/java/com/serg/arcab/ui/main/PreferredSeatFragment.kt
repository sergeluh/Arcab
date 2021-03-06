package com.serg.arcab.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.*
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth

import com.serg.arcab.R
import org.koin.android.architecture.ext.sharedViewModel
import com.serg.arcab.model.Seat
import com.serg.arcab.model.Trip
import kotlinx.android.synthetic.main.fragment_preferred_seat.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import timber.log.Timber


class PreferredSeatFragment : Fragment(), PreferredSeatRecyclerViewAdapter.Callback {

    private val reservedSeatsToMap = mutableMapOf<Int, MutableList<Seat>>()
    private val reservedSeatsFromMap = mutableMapOf<Int, MutableList<Seat>>()
    private val weekDays = mutableMapOf(Pair(1, "Sunday"),
            Pair(2, "Monday"),
            Pair(3, "Tuesday"),
            Pair(4, "Wednesday"),
            Pair(5, "Thursday"),
            Pair(6, "Friday"),
            Pair(7, "Saturday"))
    private val seats = mutableListOf<Seat>()

    @SuppressLint("SetTextI18n")
    override fun checkedChange(seat: Seat?) {
        if (seat == null) {
            navBar.nextBtn.isEnabled = false
            text_view_seat.visibility = View.INVISIBLE
        } else {
            var reservedDaysTo = ""
            var reservedDaysFrom = ""
            for ((key, value) in reservedSeatsToMap) {
                value.filter { it.id == seat.id }.forEach { reservedDaysTo += if (reservedDaysTo.isEmpty()) weekDays[key] else ", ${weekDays[key]}" }
            }
            for ((key, value) in reservedSeatsFromMap) {
                value.filter { it.id == seat.id }.forEach { reservedDaysFrom += if (reservedDaysFrom.isEmpty()) weekDays[key] else ", ${weekDays[key]}" }
            }
            //Count how many times current seat reserved
            var daysReserved = 0
            reservedSeatsToMap.values.forEach { it.forEach { if (it.id == seat.id) daysReserved++ } }
            reservedSeatsFromMap.values.forEach { it.forEach { if (it.id == seat.id) daysReserved++ } }
            if (reservedDaysTo.isEmpty() && reservedDaysFrom.isEmpty()) {
                navBar.nextBtn.isEnabled = true
                text_view_seat.text = Html.fromHtml("<b>${seat.id}</b>")
                text_view_seat.visibility = View.VISIBLE
                //Set user selected seats when new seats selected
                with(viewModel.tripOrder) {
                    preferredSeat = seat
                    preferredSeat!!.user_id = FirebaseAuth.getInstance().uid
                    preferredSeat!!.user_point = userPoint
                    for (key in resultSeatsTo!!.keys){
                        resultSeatsTo!![key] = seat.id!!
                    }
                    for (key in resultSeatsFrom!!.keys){
                        resultSeatsFrom!![key] = seat.id!!
                    }
                    Timber.d("Result seat maps: $resultSeatsTo, $resultSeatsFrom")
                }
            } else {
                var seatText = ""
                text_view_seat.text = seatText
                text_view_seat.visibility = View.VISIBLE
                with(viewModel.tripOrder) {
                    preferredSeat = seat
                    preferredSeat!!.user_id = FirebaseAuth.getInstance().uid
                    preferredSeat!!.user_point = userPoint
                    if (daysReserved < (pickMeUpDays!!.size + dropMeOffDays!!.size)) {
                        for (key in resultSeatsTo!!.keys) {
                            if (reservedDaysTo.contains(weekDays[key].toString())){
                                val seatId = getFirstAvailableSeat(key, tripIdTo!!, viewModel.tripsTo, seat.id!!)
                                resultSeatsTo!![key] = seatId!!
                                text_view_seat.text = text_view_seat.text.toString() + "\nRecommended seat for pickup at ${weekDays[key]} is $seatId"
                            }else{
                                resultSeatsTo!![key] = seat.id!!
                            }
                            Timber.d("Available seat for ${weekDays[key]} is ${resultSeatsTo!![key]}")
                        }
                        for (key in resultSeatsFrom!!.keys) {
                            if (reservedDaysFrom.contains(weekDays[key].toString())) {
                                val seatId = getFirstAvailableSeat(key, tripIdFrom!!, viewModel.tripsFrom, seat.id!!)
                                resultSeatsFrom!![key] = seatId!!
                                text_view_seat.text = text_view_seat.text.toString() + "\nRecommended seat for dropoff at ${weekDays[key]} is $seatId"
                            }else{
                                resultSeatsFrom!![key] = seat.id!!
                            }
                            Timber.d("Available seat for ${weekDays[key]} is ${resultSeatsTo!![key]}")
                        }

                        Timber.d("Available seats result: $resultSeatsTo \n $resultSeatsFrom")
                    }
                    seatText =String.format(
                            resources.getString(R.string.initial_setup_preferred_seat_reserved),
                            "<b>${seat.id}</b>",
                            if (reservedDaysTo.isNotEmpty()) " pickup at $reservedDaysTo" else "",
                            if (reservedDaysFrom.isNotEmpty()) " dropoff at $reservedDaysFrom" else "") + text_view_seat.text
                    text_view_seat.text = Html.fromHtml(seatText)
                }
                //Set button enabled if selected seat is free at least at one of selected days
                navBar.nextBtn.isEnabled = daysReserved < (viewModel.tripOrder.pickMeUpDays!!.size
                        + viewModel.tripOrder.dropMeOffDays!!.size)
            }
        }
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preferred_seat, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Timber.d("Trip ids in model: ${viewModel.tripOrder.tripIdTo}, ${viewModel.tripOrder.tripIdFrom}")
        with(viewModel.tripOrder) {
            if (pickMeUpDays!!.size > 0) {
                getReservedSeats(tripIdTo!!, viewModel.tripsTo, viewModel.tripOrder.pickMeUpDays, reservedSeatsToMap)
            }
            if (dropMeOffDays!!.size > 0) {
                getReservedSeats(tripIdFrom!!, viewModel.tripsFrom, viewModel.tripOrder.dropMeOffDays, reservedSeatsFromMap)
            }
        }


        navBar.backBtn.setOnClickListener {
            viewModel.onBackClicked()
            //Remove user preferred seat data
            viewModel.tripOrder.preferredSeat = null
        }

        navBar.nextBtn.setOnClickListener {
            Timber.d("Preferred seats selected: ${viewModel.tripOrder}")
            viewModel.onGoToPaymentPlanClicked()
        }

        navBar.nextBtn.isEnabled = false

        seats.clear()
        val seatCodes = arrayOf("D", "C", "B", "A")
        for (i in 4 downTo 1) {
            for (code in seatCodes.iterator()) {
                val seatId = "$i$code"
                seats.add(Seat(seatId))
            }
        }

        val seatAdapter = PreferredSeatRecyclerViewAdapter(this, seats,
                viewModel.tripOrder.preferredSeat?.id)

        preferred_seat_recycler_view.layoutManager = GridLayoutManager(context, seatCodes.size)
        preferred_seat_recycler_view.adapter = seatAdapter

    }

    override fun onResume() {
        super.onResume()
        viewModel.tripOrder.resultSeatsTo = mutableMapOf()
        viewModel.tripOrder.pickMeUpDays?.forEach {
            viewModel.tripOrder.resultSeatsTo!![it] = ""
        }
        viewModel.tripOrder.resultSeatsFrom = mutableMapOf()
        viewModel.tripOrder.dropMeOffDays?.forEach {
            viewModel.tripOrder.resultSeatsFrom!![it] = ""
        }
        //Show text_view_seat and populate it with user seats data when user comes back from on of the other fragments
        if (viewModel.tripOrder.preferredSeat != null) {
            text_view_seat.visibility = View.VISIBLE
            text_view_seat.text = viewModel.tripOrder.preferredSeat?.id
            navBar.nextBtn.isEnabled = true
        }
    }

    //Method for creating reserved seat list from trip object
    private fun getReservedSeats(id: Int, trips: MutableList<Trip>, selectedDays: MutableList<Int>?, target: MutableMap<Int, MutableList<Seat>>) {
        val tempTrips = trips.filter { it.id == id }
        if (tempTrips.isNotEmpty()) {
            val trip = trips[0]
            trip.booked_days?.filter {
                @Suppress("SENSELESS_COMPARISON")
                it != null
            }?.forEach {
                Timber.d("Current booked day $it")
                if (selectedDays?.contains(it.index)!! && it.seats != null) {
                    target[it.index!!] = it.seats?.values!!.toMutableList()
                }
            }
        }
    }

    //Method that returns seat id if its available for selected day
    private fun getFirstAvailableSeat(dayIndex: Int, tripId: Int, trips: MutableList<Trip>, seatId: String): String?{
        val tripSeats = trips.first { it.id == tripId }.booked_days!![dayIndex].seats?.values?.map { it.id }
        Timber.d("Trip seats: $tripSeats")
        return seats.first{ !tripSeats!!.contains(it.id) && it.id != seatId}.id
    }

    companion object {

        const val TAG = "PickupPointFragment"

        @JvmStatic
        fun newInstance() = PreferredSeatFragment()
    }
}
