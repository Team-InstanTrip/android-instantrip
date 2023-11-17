package com.instantrip.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val _isVisible = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isVisible: LiveData<Boolean> = _isVisible

    private val _isUserLogined = MutableLiveData<Boolean>()
    val isUserLogined: LiveData<Boolean> = _isUserLogined

    fun toggleVisibility() {
        _isVisible.value = _isVisible.value != true
    }

    fun setIsUserLogined(isLogined: Boolean) {
        _isUserLogined.value = isLogined
    }


}