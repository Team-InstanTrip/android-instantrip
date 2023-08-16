package com.instantrip.data.model

import com.google.gson.annotations.SerializedName

data class UserNickName (
    @SerializedName("userName")
    val userName: String?
)