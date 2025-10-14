package com.example.groot

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    // We will now start the state as 'false' (logged out)
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // The listener that will update our state when a login happens
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _isLoggedIn.value = firebaseAuth.currentUser != null
    }

    init {
        // --- THIS IS THE KEY CHANGE ---
        // Sign out any previously logged-in user as soon as the app starts.
        // This ensures we always begin in a logged-out state.
        auth.signOut()

        // After signing out, we start listening for any NEW login events.
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}