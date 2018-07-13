package com.serg.arcab.model

import com.google.gson.annotations.SerializedName

/**
 * Model that stores common_points data from database
 */
data class CommonPoint(

        @SerializedName("address")
        var address: String? = null,

        @SerializedName("id")
        var id: Int? = null,

        @SerializedName("latitude")
        var latitude: Double? = null,

        @SerializedName("longitude")
        var longitude: Double? = null,

        @SerializedName("title")
        var title: String? = null)