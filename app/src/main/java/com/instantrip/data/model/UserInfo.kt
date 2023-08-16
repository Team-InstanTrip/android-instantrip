package com.instantrip.data.model


import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("email")
    val email: String?,
    @SerializedName("kakaoUserNumber")
    val kakaoUserNumber: Int,
    @SerializedName("role")
    val role: String?,
    @SerializedName("userName")
    val userName: String?
)