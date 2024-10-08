package com.app.myrickshawparent.prefrence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyDataStore @Inject constructor(private val context: Context) {

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    }

    object PrefKey {
        const val USER_ID = "user_id"
        const val IS_SIGN_IN = "sign_in"
        const val API_KEY = "api_key"
        const val EMAIL = "email"
        const val PHONE = "phone"
        const val PROFILE = "profile"
        const val FIRST_NAME = "first_name"
        const val FULL_NAME = "full_name"
        const val LAST_NAME = "last_name"
        const val TOKEN = "token"
    }

    val isLogin = booleanPreferencesKey(PrefKey.IS_SIGN_IN)
    val userId = stringPreferencesKey(PrefKey.USER_ID)
    val apiKey = stringPreferencesKey(PrefKey.API_KEY)
    val email = stringPreferencesKey(PrefKey.EMAIL)
    val phone = stringPreferencesKey(PrefKey.PHONE)
    val firstName = stringPreferencesKey(PrefKey.FIRST_NAME)
    val fullName = stringPreferencesKey(PrefKey.FULL_NAME)
    val lastName = stringPreferencesKey(PrefKey.LAST_NAME)
    val token = stringPreferencesKey(PrefKey.TOKEN)
    val profile = stringPreferencesKey(PrefKey.PROFILE)


    suspend fun setStringData(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun getStringData(key: Preferences.Key<String>): Flow<String> {
        return context.dataStore.data.map {
            it[key] ?: ""
        }
    }

    fun getIntData(key: Preferences.Key<Int>): Flow<Int> {
        return context.dataStore.data.map {
            it[key] ?: -1
        }
    }

    suspend fun setIntData(key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun getBooleanData(key: Preferences.Key<Boolean>): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: false
        }
    }

    suspend fun setBooleanData(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun logoutUser() {
        context.dataStore.edit {
            it.clear()
        }
    }
}





