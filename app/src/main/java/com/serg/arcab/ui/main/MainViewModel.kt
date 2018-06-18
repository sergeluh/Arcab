package com.serg.arcab.ui.main

import com.serg.arcab.base.BaseViewModel
import com.serg.arcab.utils.SingleLiveEvent

class MainViewModel: BaseViewModel() {

    val backAction = SingleLiveEvent<Unit>()
    val goToLinkId = SingleLiveEvent<Unit>()
    val goToPlaces = SingleLiveEvent<Unit>()

    fun onGoToLinkIdClicked() {
        goToLinkId.call()
    }

    fun onBackClicked() {
        backAction.call()
    }

    fun onGoToPlacesClicked() {
        goToPlaces.call()
    }
}