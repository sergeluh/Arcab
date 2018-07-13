package com.serg.arcab.model

import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName

data class Seat constructor(
        @SerializedName("id")
        var id: String? = null,

        @SerializedName("user_id")
        var user_id: String? = null,

        @SerializedName("user_point")
        var user_point: UserPoint? = null,

        //Flag for detecting whether the user pick this day
        @get:Exclude
        var isSelected: Boolean = false)