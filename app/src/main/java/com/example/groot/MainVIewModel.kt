package com.example.groot

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    // This holds the login state in memory.
    // It starts as 'false' (logged out).
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    // The LoginScreen will call this function to update the state.
    fun onLoginSuccess() {
        _isLoggedIn.value = true
    }
}