// File: app/src/main/java/com/example/groot/data/UserPreferencesRepository.kt

package com.example.groot.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// This line creates a single, shared file named "user_prefs.pb" on the user's device.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * This class handles reading and writing data to the DataStore file.
 * It's the "long-term memory" manager for your app.
 */
class UserPreferencesRepository(private val context: Context) {

    // This is a companion object to hold the "keys" for our data.
    // Think of a key as the name of the variable you're saving.
    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    /**
     * This is for READING the login state.
     * It returns a Flow, which is a stream of data. The UI can "listen" to this stream.
     * If the value is not found in the file, it defaults to 'false'.
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    /**
     * This is for WRITING the login state.
     * It's a suspend function because writing to a file is an async operation.
     * It takes a boolean and saves it to the file under our IS_LOGGED_IN key.
     */
    suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }
}