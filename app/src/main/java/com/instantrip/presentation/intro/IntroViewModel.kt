package com.instantrip.presentation.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause

class IntroViewModel: ViewModel() {
    private val _kakaoLoginCallback: MutableLiveData<(OAuthToken?, Throwable?) -> Unit> = MutableLiveData()
    val kakaoLoginCallback: LiveData<(OAuthToken?, Throwable?) -> Unit> get() = _kakaoLoginCallback

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
//            getKakaoUserInfo()
        }
    }
}