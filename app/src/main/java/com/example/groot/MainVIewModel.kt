package com.example.groot

import android.util.Log // <-- ADD THIS IMPORT
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // <-- ADD THIS IMPORT
import com.example.groot.data.PlantRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging // <-- ADD THIS IMPORT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch // <-- ADD THIS IMPORT

class MainViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    // This remains the same, starting the state as 'false' (logged out)
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // This repository instance is also correct
    private val repository = PlantRepository()

    // --- WE WILL MODIFY THE LISTENER ---
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _isLoggedIn.value = user != null

        // --- NEW LOGIC ADDED HERE ---
        if (user != null) {
            // If a user has just logged in (user is not null),
            // get their new FCM token and save it to Firestore.
            getAndSaveFCMToken(user.uid)
        }
    }

    init {
        // This logic remains the same to ensure the login screen always shows first.
        auth.signOut()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    // --- NEW FUNCTION TO HANDLE FCM TOKEN ---
    private fun getAndSaveFCMToken(userId: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("FCM", "Current FCM Token: $token")

            // Save the token to Firestore using a coroutine in the viewModelScope
            viewModelScope.launch {
                repository.saveFCMToken(userId, token)
            }
        }
    }
}