package com.instantrip.data.api

import com.instantrip.data.model.UserInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("/api/users/{userId}")
    suspend fun getUserInfo(@Path("userId") userId: String) : Response<UserInfo>

    @GET("/api/users/logout")
    suspend fun logout()

    @GET("/api/users/oauth")
    suspend fun login(@Query("code") code: String): Response<UserInfo>

    @GET("/api/users/sign-up")
    suspend fun signUp()
}