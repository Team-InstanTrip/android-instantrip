package com.instantrip.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.instantrip.R
import com.instantrip.data.model.SignUpUserInfo
import com.instantrip.data.model.UserInfo
import com.instantrip.databinding.ActivityNicknameBinding
import com.instantrip.presentation.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class NickNameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNicknameBinding
    private val viewModel: NickNameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nickname)

        setListeners()
        setViewModels()
    }

    private fun setListeners() {
        binding.btnAutoNickname.setOnClickListener {
            viewModel.makeRandomNickName()
        }

        binding.btnNext.setOnClickListener {
            val userInfo = intent.getSerializableExtra("userInfo") as UserInfo
            val nickName = binding.etNickname.text.toString()
            val newSignUpUserInfo = SignUpUserInfo(userInfo.email!!, userInfo.kakaoUserNumber, "", nickName)
            CoroutineScope(Dispatchers.IO).launch {
                val result = async {
                    viewModel.sendNickName(newSignUpUserInfo)
                }.await()

                withContext(Dispatchers.Main) {
                    if (result) {
                        Timber.d("회원가입 성공!")
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Timber.d("회원가입 실패!")
                    }
                }
            }
        }
    }

    private fun setViewModels() {
        viewModel.nickName.observe(this) {
            binding.etNickname.setText(it)
        }
    }
}