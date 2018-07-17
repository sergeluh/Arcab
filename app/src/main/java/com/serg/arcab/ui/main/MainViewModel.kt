package com.serg.arcab.ui.main

import com.serg.arcab.model.Trip
import com.serg.arcab.model.TripOrder
import com.serg.arcab.base.BaseViewModel
import com.serg.arcab.model.CommonPoint
import com.serg.arcab.model.University
import com.serg.arcab.utils.SingleLiveEvent

class MainViewModel: BaseViewModel() {

    //Trip model that stores data about trips
    val tripsTo = mutableListOf<Trip>()
    val tripsFrom = mutableListOf<Trip>()
    var commonPoints: MutableList<CommonPoint>? = null
    var universities: University? = null
    //Trip order model for storing data about the trip
    val tripOrder = TripOrder()

    val backAction = SingleLiveEvent<Unit>()
    val goToLinkId = SingleLiveEvent<Unit>()
    val goToPlaces = SingleLiveEvent<Unit>()
    val goToPickupPoint = SingleLiveEvent<Unit>()
    val goToPickupTiming = SingleLiveEvent<Unit>()
    val goToPreferredSeat = SingleLiveEvent<Unit>()
    val goToPaymentPlan = SingleLiveEvent<Unit>()
    val goToResult = SingleLiveEvent<Unit>()
    val confirmOrder = SingleLiveEvent<Unit>()
    val goToNotAvailableFragment = SingleLiveEvent<Unit>()
    val letMeIn = SingleLiveEvent<String>()
    val hideKeyboard = SingleLiveEvent<Unit>()

    fun onGoToLinkIdClicked() {
        goToLinkId.call()
    }

    fun onBackClicked() {
        backAction.call()
    }

    fun onGoToPlacesClicked() {
        goToPlaces.call()
    }

    fun onGoToPickupPointClicked() {
        goToPickupPoint.call()
    }

    fun onGoToPickupTimingClicked() {
        goToPickupTiming.call()
    }

    fun onGoToPreferredSeatClicked() {
        goToPreferredSeat.call()
    }

    fun onGoToPaymentPlanClicked() {
        goToPaymentPlan.call()
    }

    fun onGoToResultClicked(){
        goToResult.call()
    }

    fun onConfirmOrderClicked(){
        confirmOrder.call()
    }

    fun onGoNotToAvailableFragmentClicked(){
        goToNotAvailableFragment.call()
    }

    fun onLetMeInClicked(organization: String){
        letMeIn.callWithValue(organization)
    }

    fun onHideKeyboard(){
        hideKeyboard.call()
    }
}