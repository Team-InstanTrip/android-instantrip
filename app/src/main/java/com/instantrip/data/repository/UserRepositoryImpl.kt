package com.instantrip.data.repository

import com.instantrip.data.model.UserInfo
import com.instantrip.domain.repository.UserRepository

class UserRepositoryImpl: UserRepository {
    val userRemoteDataSource: UserRemoteDataSource = UserRemoteDataSourceImpl()
    override suspend fun getUserInfo(): UserInfo {
        TODO("Not yet implemented")
    }

    override suspend fun login(code: String): UserInfo? {
        return  loginFromAPI(code)
    }

    private suspend fun loginFromAPI(code: String): UserInfo? {
        try {
            val response = userRemoteDataSource.login(code)
            val body = response.body()
            if (body != null) {
                return UserInfo(null, null, null, body.kakaoUserNumber, null, null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override suspend fun logout(): UserInfo? {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(): UserInfo? {
        TODO("Not yet implemented")
    }

}