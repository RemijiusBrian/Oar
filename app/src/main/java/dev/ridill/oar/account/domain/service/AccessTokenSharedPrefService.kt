package dev.ridill.oar.account.domain.service

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccessTokenSharedPrefService(
    private val sharedPref: SharedPreferences
) : AccessTokenService {
    override suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        sharedPref.getString(ACCESS_TOKEN_KEY, null)
    }

    override suspend fun updateAccessToken(token: String?) = withContext(Dispatchers.IO) {
        with(sharedPref.edit()) {
            putString(ACCESS_TOKEN_KEY, token)
            apply()
        }
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
    }
}