package com.instantrip.data.repository

import com.instantrip.data.api.RetrofitInstance
import com.instantrip.data.api.UserService
import com.instantrip.data.model.UserInfo
import retrofit2.Response

class UserRemoteDataSourceImpl: UserRemoteDataSource {
    val userService = RetrofitInstance.getRetrofitInstance().create(UserService::class.java)

    override suspend fun login(code: String): Response<UserInfo> = userService.login(code)
}