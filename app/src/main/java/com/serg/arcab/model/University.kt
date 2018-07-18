package com.serg.arcab.model

import com.google.gson.annotations.SerializedName

/**
 * Data class for storing data about university from database
 */
data class University(
        @SerializedName("id")
        var id: Int? = null,

        @SerializedName("latitude")
        var latitude: Double? = null,

        @SerializedName("longitude")
        var longitude: Double? = null,

        @SerializedName("schedule")
        var schedule: Schedule? = null,

        @SerializedName("sufix")
        var sufix: String? = null,

        @SerializedName("title")
        var title: String? = null

        ){
    data class Schedule(
            @SerializedName("trips_from")
            var trips_from: MutableList<TripId>? = null,

            @SerializedName("trips_to")
            var trips_to: MutableList<TripId>? = null
    )

    data class TripId(
            @SerializedName("trip_id")
            var trip_id: Int? = null
    )
}