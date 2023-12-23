package com.instantrip.presentation.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.instantrip.R
import com.instantrip.databinding.ActivityIntroBinding
import com.instantrip.util.Utils
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private val viewModel: IntroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        Utils.getKeyHash(this)

        initViewModels()

        binding.btnLoginKakao.setOnClickListener {
            viewModel.loginKakao()
        }
    }

    private fun initViewModels() {
        viewModel.kakaoLoginCallback.observe(this) {
            kakaoLogin(it)
        }
        viewModel.loginStatus.observe(this) {
            when (it) {
                LoginStatus.JOINED -> {
                    setResult(RESULT_OK)
                    finish()
                }
                LoginStatus.NEWBIE -> {
                    val intent = Intent(this, NickNameActivity::class.java)
                    intent.putExtra("userInfo", viewModel.userInfo.value)
                    startActivity(intent)
                }
            }
        }
    }

    private fun kakaoLogin(callback: (OAuthToken?, Throwable?) -> Unit) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }
}