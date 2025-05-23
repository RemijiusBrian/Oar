package dev.ridill.oar.account.domain.repository

import android.app.PendingIntent
import android.content.Intent
import dev.ridill.oar.account.domain.model.AuthState
import dev.ridill.oar.account.domain.model.UserAccount
import dev.ridill.oar.account.presentation.util.AuthorizationService
import dev.ridill.oar.core.domain.model.DataError
import dev.ridill.oar.core.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getSignedInAccount(): UserAccount?
    fun getAuthState(): Flow<AuthState>
    suspend fun signUserInWithToken(idToken: String): Result<Unit, DataError>
    suspend fun signUserOut(): Result<Unit, DataError>
    suspend fun authorizeUserAccount(): Result<PendingIntent?, AuthorizationService.AuthorizationError>
    suspend fun decodeAuthorizationResult(intent: Intent): Result<Unit, AuthorizationService.AuthorizationError>
    suspend fun deleteAccount(): Result<Unit, DataError>
}