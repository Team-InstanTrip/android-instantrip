package com.instantrip.util

import android.content.Context
import com.kakao.sdk.common.util.Utility
import timber.log.Timber

object Utils {
    fun getKeyHash(context: Context) {
        var keyHash = Utility.getKeyHash(context)
        Timber.d("keyHash=$keyHash")
    }
}