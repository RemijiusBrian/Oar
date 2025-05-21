package dev.ridill.oar.account.presentation.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import dev.ridill.oar.core.domain.util.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultAuthorizationService(
    context: Context
) : AuthorizationService {
    private val authorizationClient = Identity.getAuthorizationClient(context)

    private val scopes
        get() = listOf(Scopes.DRIVE_APPFOLDER)
            .map { Scope(it) }

    @Throws(AuthorizationNeedsResolutionThrowable::class)
    override suspend fun getIntentSenderForAuthorization(): AuthorizationResult =
        withContext(Dispatchers.IO) {
            val request = AuthorizationRequest.Builder()
                .setRequestedScopes(scopes)
                .build()

            val result = authorizationClient.authorize(request).await()
            if (result.hasResolution())
                throw AuthorizationNeedsResolutionThrowable(result.pendingIntent)

            val accessToken = result.accessToken
            val pendingIntent = result.pendingIntent
            AuthorizationResult(accessToken, pendingIntent)
        }

    @Throws(AuthorizationNeedsResolutionThrowable::class, AuthorizationFailedThrowable::class)
    override fun decodeAccessTokenFromIntent(intent: Intent?): String {
        val result = authorizationClient.getAuthorizationResultFromIntent(intent)
        if (result.hasResolution()) throw AuthorizationNeedsResolutionThrowable(result.pendingIntent)
        val accessToken = result.accessToken ?: throw AuthorizationFailedThrowable()
        logD { "AccessToken - $accessToken" }
        return accessToken
    }

    /*companion object {
        private const val OAUTH_SCOPE_STRING =
            "oauth2:${Scopes.DRIVE_APPFOLDER} ${Scopes.DRIVE_FILE}"
    }*/
}

class AuthorizationNeedsResolutionThrowable(val pendingIntent: PendingIntent?) : Throwable()
class AuthorizationFailedThrowable : Throwable()