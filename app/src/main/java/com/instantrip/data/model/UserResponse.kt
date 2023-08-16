package com.instantrip.data.model


import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("data")
    val data: UserInfo,
    @SerializedName("httpStatus")
    val httpStatus: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("responseCode")
    val responseCode: String
)