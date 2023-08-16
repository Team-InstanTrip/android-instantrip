package com.instantrip.data.repository

import com.instantrip.data.model.UserInfo
import retrofit2.Response

interface UserRemoteDataSource {
    suspend fun login(code: String): Response<UserInfo>
}