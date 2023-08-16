package com.instantrip.data.model

import java.io.Serializable

/**
 * 카카오 로그인 요청
 */
data class LoginRequest (
    val provider: String,
    val code: String,
    val device: String? = "Android",
    val firebaseToken: String?
) : Serializable