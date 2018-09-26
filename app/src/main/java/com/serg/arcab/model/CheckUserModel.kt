package com.serg.arcab.model

import com.google.gson.annotations.SerializedName

data class CheckUserModel(@SerializedName("isRegistered") var isRegistered: Boolean? = null)