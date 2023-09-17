package com.instantrip.domain.repository

import com.instantrip.data.model.SignUpUserInfo
import com.instantrip.data.model.UserInfo

interface UserRepository {
    suspend fun getUserInfo(): UserInfo
    suspend fun login(code: String): UserInfo?
    suspend fun logout(): UserInfo?
    suspend fun signUp(signUpUserInfo: SignUpUserInfo): Boolean
}