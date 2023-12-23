package com.instantrip.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.instantrip.data.model.UserInfo
import com.instantrip.data.repository.UserRepositoryImpl
import com.instantrip.domain.repository.UserRepository
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import timber.log.Timber

class IntroViewModel: ViewModel() {
    private val userRepository: UserRepository = UserRepositoryImpl()

    private val _kakaoLoginCallback: MutableLiveData<(OAuthToken?, Throwable?) -> Unit> = MutableLiveData()
    val kakaoLoginCallback: LiveData<(OAuthToken?, Throwable?) -> Unit> get() = _kakaoLoginCallback

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _loginStatus: MutableLiveData<LoginStatus> = MutableLiveData()
    val loginStatus: LiveData<LoginStatus> get() = _loginStatus

    private val _userInfo: MutableLiveData<UserInfo> = MutableLiveData()
    val userInfo: LiveData<UserInfo> get() = _userInfo

    fun loginKakao() {
        // 카카오계정으로 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        error("접근이 거부 됨(동의 취소)")
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        error("유효하지 않은 앱")
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        error("인증 수단이 유효하지 않아 인증할 수 없는 상태")
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        error("요청 파라미터 오류")
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        error("유효하지 않은 scope ID")
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        error("설정이 올바르지 않음(android key hash)")
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        error("서버 내부 에러")
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        error("앱이 요청 권한이 없음")
                    }
                    else -> { // Unknown
                        error("기타 에러")
                    }
                }
            } else if (token != null) {
                Timber.d("카카오계정 로그인 성공 token=${token.accessToken}")
                getKakaoUserInfo()
            }
        }

        _kakaoLoginCallback.postValue(callback)
    }

    private fun getKakaoUserInfo() {
        // 사용자 정보 요청 (기본)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                error("사용자 정보 요청 실패")
            }
            else if (user != null) {
                Timber.i("사용자 정보 요청 성공" +
                        "\n회원번호: ${user.id}" +
                        "\n이메일: ${user.kakaoAccount?.email}")
                _userInfo.postValue(UserInfo(null, user.kakaoAccount?.email, null, user.id!!, null, null, null))
            }
        }
        // 토큰 정보 보기
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
              error(error.toString())
            } else if (tokenInfo != null) {
                Timber.d("토큰정보 있음! code = ${tokenInfo.id}")
                CoroutineScope(IO).launch {
                    login("${tokenInfo.id}")
                }
            }
        }
    }

    suspend fun login(code: String) {
        val response = userRepository.login(code)
        if (response != null) {
            // 기 가입된 사람
            Timber.d("kakaoUserNumber = ${response.kakaoUserNumber}")
            _loginStatus.postValue(LoginStatus.JOINED)
        } else {
            // 닉네임 받는 페이지로 이동
            Timber.d("response = null")
            _loginStatus.postValue(LoginStatus.NEWBIE)
        }
    }

    fun error(msg: String) {
        Timber.e(msg)
        _errorMessage.postValue(msg)
    }
}

/**
 * JOINED : 기존 가입자
 * NEWBIE : 신규 가입자
 */
enum class LoginStatus {
    JOINED, NEWBIE
}