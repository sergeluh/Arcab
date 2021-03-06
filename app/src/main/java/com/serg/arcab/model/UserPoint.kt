package com.serg.arcab.model

import com.google.gson.annotations.SerializedName

data class UserPoint constructor(
        @SerializedName("id")
        var id: Int? = null,

        @SerializedName("address")
        var address: String? = null,

        @SerializedName("latitude")
        var latitude: Double? = null,

        @SerializedName("longitude")
        var longitude: Double? = null,

        @SerializedName("title")
        var title: String? = null,

        @SerializedName("type")
        var type: String? = null
)