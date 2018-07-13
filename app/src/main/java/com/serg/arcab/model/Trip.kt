package com.serg.arcab.model

import com.google.gson.annotations.SerializedName

/**
 * Trip model that will be read from firebase
 */
data class Trip(
        @SerializedName("id")
        var id: Int? = null,

        @SerializedName("time")
        var time: String? = null,

//        @SerializedName("booked_days")
//        var booked_days: MutableMap<String, BookedDay>? = null,

        @SerializedName("booked_days")
        var booked_days: MutableList<BookedDay>? = null,

        @SerializedName("departure")
        var departure: UserPoint? = null,

        @SerializedName("destination")
        var destination: UserPoint? = null

){

    data class BookedDay(
            @SerializedName("index")
            var index: Int? = null,

            @SerializedName("title")
            var title: String? = null,

            @SerializedName("seats")
            var seats: MutableMap<String, Seat>? = null
    )

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        other as Trip
        return super.equals(this.booked_days!! == other.booked_days!!
                && this.departure == other.departure && this.destination == other.destination
                && this.id == other.id && this.time == other.time)
    }
}