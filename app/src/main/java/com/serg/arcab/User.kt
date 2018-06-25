package com.serg.arcab

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("birth_date")
        var birth_date: Long? = null,

        @SerializedName("email")
        var email: String? = null,

        @SerializedName("emirates_id")
        var emirates_id: String? = null,

        @SerializedName("first_name")
        var first_name: String? = null,

        @SerializedName("last_name")
        var last_name: String? = null,

        @SerializedName("flags")
        var flags: Flags = Flags(),

        @SerializedName("gender")
        var gender: Int? = null,

        @SerializedName("password")
        var password: String? = null,

        @SerializedName("phone_number")
        var phone_number: String? = null,

        @SerializedName("terms")
        var terms: Terms = Terms()
) {
        data class Flags(
                @SerializedName("isInitialSetupComplete")
                var isInitialSetupComplete: Boolean = false,

                @SerializedName("isRegistered")
                var isRegistered: Boolean = false
        )

        data class Terms(
                @SerializedName("directly_contact")
                var directly_contact: Boolean = false,

                @SerializedName("search_ads")
                var search_ads: Boolean = false,

                @SerializedName("usage_data")
                var usage_data: Boolean = false,

                @SerializedName("usage_stats")
                var usage_stats: Boolean = false
        )
}