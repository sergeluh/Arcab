package com.serg.arcab.ui.main

import android.arch.lifecycle.MutableLiveData
import com.serg.arcab.User
import com.serg.arcab.model.Trip
import com.serg.arcab.model.TripOrder
import com.serg.arcab.base.BaseViewModel
import com.serg.arcab.data.PrefsManager
import com.serg.arcab.model.CommonPoint
import com.serg.arcab.model.University
import com.serg.arcab.utils.SingleLiveEvent

class MainViewModel(private val prefsManager: PrefsManager): BaseViewModel() {

    //Trip model that stores data about trips
    var tripsTo = mutableListOf<Trip>()
    var tripsFrom = mutableListOf<Trip>()
    var commonPoints: MutableList<CommonPoint>? = null
    var university: University? = null
    //Trip order model for storing data about the trip
    var tripOrder = TripOrder()

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
    val goToScan = SingleLiveEvent<Unit>()

    val user = MutableLiveData<User>()

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

    fun wasAppOpened(): Boolean{
        return prefsManager.wasAppOpen()
    }

    fun setAppOpened(wasOpened: Boolean){
        prefsManager.setAppWasOpen(wasOpened)
    }

    fun getReschedule(): Boolean{
        return prefsManager.getRescheduleTimings()
    }

    fun setReschedule(reschedule: Boolean){
        prefsManager.setRescheduleTimings(reschedule)
    }

    fun onGoToScanClicked(){
        goToScan.call()
    }
}