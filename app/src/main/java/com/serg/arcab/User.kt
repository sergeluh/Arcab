package com.serg.arcab

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("birth_date")
        var birthDate: Long? = null,

        @SerializedName("email")
        var email: String? = null,

        @SerializedName("emirates_id")
        var emiratesId: String? = null,

        @SerializedName("first_name")
        var firstName: String? = null,

        @SerializedName("last_name")
        var lastName: String? = null,

        @SerializedName("flags")
        var flags: Flags = Flags(),

        @SerializedName("gender")
        var gender: Int? = null,

        @SerializedName("password")
        var password: String? = null,

        @SerializedName("phone_number")
        var phoneNumber: String? = null,

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
                var directlyContact: Boolean = false,

                @SerializedName("search_ads")
                var searchAds: Boolean = false,

                @SerializedName("usage_data")
                var usageData: Boolean = false,

                @SerializedName("usage_stats")
                var usageStats: Boolean = false
        )
}