package com.instantrip.data.api

import com.instantrip.data.model.UserInfo
import retrofit2.Response
import retrofit2.http.GET

interface UserService {
    @GET("/api/users/{userId}")
    suspend fun getUserInfo() : Response<UserInfo>

    @GET("/api/users/logout")
    suspend fun logout() : Response<UserInfo>

    @GET("/api/users/oauth")
    suspend fun signUp()
}