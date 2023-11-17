package com.instantrip.data.model


import com.google.gson.annotations.SerializedName

data class SignUpUserInfo(
    @SerializedName("email")
    val email: String?,
    @SerializedName("kakaoUserNumber")
    val kakaoUserNumber: Long,
    @SerializedName("role")
    val role: String,
    @SerializedName("userName")
    val userName: String
)