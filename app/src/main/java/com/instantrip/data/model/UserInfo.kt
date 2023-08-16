package com.instantrip.data.model


import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("activeStatus")
    val activeStatus: Boolean?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("joinDate")
    val joinDate: String?,
    @SerializedName("kakaoUserNumber")
    val kakaoUserNumber: Long,
    @SerializedName("role")
    val role: String?,
    @SerializedName("userId")
    val userId: Int?,
    @SerializedName("userName")
    val userName: String?
)