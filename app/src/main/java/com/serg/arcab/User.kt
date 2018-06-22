package com.serg.arcab

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("birth_date")
        var birthDate: Long?,

        @SerializedName("email")
        var email: String?,

        @SerializedName("emirates_id")
        var emiratesId: String?
)