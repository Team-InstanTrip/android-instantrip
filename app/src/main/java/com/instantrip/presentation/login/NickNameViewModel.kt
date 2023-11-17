package com.instantrip.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.instantrip.data.model.SignUpUserInfo
import com.instantrip.data.repository.UserRepositoryImpl
import com.instantrip.domain.repository.UserRepository

class NickNameViewModel: ViewModel() {
    private val userRepository: UserRepository = UserRepositoryImpl()

    private val _nickName: MutableLiveData<String> = MutableLiveData()
    val nickName: LiveData<String> get() = _nickName

    fun makeRandomNickName() {
        // Todo: 서버에서 닉네임 받아오기
        val temp = "랜덤닉네임"
        _nickName.postValue(temp)
    }

    suspend fun sendNickName(signUpUserInfo: SignUpUserInfo): Boolean {
        val result = userRepository.signUp(signUpUserInfo)
        return result
    }
}