package com.instantrip.data.repository

import com.instantrip.data.model.SignUpUserInfo
import com.instantrip.data.model.UserInfo
import retrofit2.Response

interface UserRemoteDataSource {
    suspend fun login(code: String): Response<UserInfo>
     suspend fun signUp(signUpUserInfo: SignUpUserInfo): Response<Void>
}