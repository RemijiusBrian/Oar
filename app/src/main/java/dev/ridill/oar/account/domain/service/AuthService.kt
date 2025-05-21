package dev.ridill.oar.account.domain.service

import dev.ridill.oar.account.domain.model.AuthState
import dev.ridill.oar.account.domain.model.UserAccount
import kotlinx.coroutines.flow.Flow

interface AuthService {
    fun getSignedInAccount(): UserAccount?
    fun getAuthStateFlow(): Flow<AuthState>
    suspend fun signinUserWithIdToken(idToken: String)
    suspend fun signUserOut()
    suspend fun deleteAccount()
}