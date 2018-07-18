package com.serg.arcab.model

import com.google.android.gms.maps.model.LatLng

/**
 * Data class that stores data about current order
 */
data class TripOrder(
        var linkId: String? = null,

        var tripIdTo: Int? = null,

        var tripIdFrom: Int? = null,

        var currentLocation: LatLng? = null,

        var address: String? = null,

        var dayIndexesTo: MutableList<Int>? = null,

        var dayIndexesFrom: MutableList<Int>? = null,

        var pickMeUpAt: TimingItem? = null,

        var pickMeUpAtIndex: Int? = null,

        var pickMeUpDays: MutableList<Int>? = null,

        var dropMeOffAt: TimingItem? = null,

        var dropMeOfAtIndex: Int? = null,

        var dropMeOffDays: MutableList<Int>? = null,

        var userPoint: UserPoint? = null,

        var preferredSeat: Seat? = null,

        var resultSeatsTo: MutableMap<Int, String>? = null,

        var resultSeatsFrom: MutableMap<Int, String>? = null,

        var resultMessage: String? = null,

        var pickupMessage: String? = null
        )