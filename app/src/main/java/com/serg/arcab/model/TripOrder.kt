package com.serg.arcab.model

import com.google.android.gms.maps.model.LatLng

/**
 * Data class that stores data about current order
 */
data class TripOrder(
        var linkId: String? = null,

        var currentLocation: LatLng? = null,

        var address: String? = null,

        var dayIndex: Int? = null,

        var pickMeUpAt: TimingItem? = null,

        var pickMeUpAtIndex: Int? = null,

        var dropMeOffAt: TimingItem? = null,

        var dropMeOfAtIndex: Int? = null,

        var userPoint: UserPoint? = null,

        var preferredSeat: Seat? = null,

        var resultMessage: String? = null,

        var pickupMessage: String? = null
        )