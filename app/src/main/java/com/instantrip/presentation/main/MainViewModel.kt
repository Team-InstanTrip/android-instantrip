package com.instantrip.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val _isVisible = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isVisible: LiveData<Boolean> = _isVisible

    fun toggleVisibility() {
        _isVisible.value = _isVisible.value != true
    }
}