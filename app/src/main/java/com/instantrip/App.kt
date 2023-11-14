package com.instantrip

import android.app.Application
import com.instantrip.util.PreferenceUtil
import com.kakao.sdk.common.KakaoSdk
import timber.log.Timber

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        // Kakao SDK 초기화
        KakaoSdk.init(this, "35e79faafc6f538f208f8c4e964638ff")
        PreferenceUtil.init(this)
    }
}