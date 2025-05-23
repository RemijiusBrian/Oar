package dev.ridill.oar.account.presentation.util

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.model.Result
import dev.ridill.oar.core.domain.util.logE
import dev.ridill.oar.core.domain.util.rethrowIfCoroutineCancellation
import dev.ridill.oar.core.ui.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultCredentialService(
    private val context: Context
) : CredentialService {

    private val credentialManager = CredentialManager.create(context)

    override suspend fun startGetCredentialFlow(
        filterByAuthorizedUsers: Boolean,
        activityContext: Context,
    ): Result<String, CredentialService.CredentialError> = getCredential(
        request = buildGoogleIdOptionRequest(filterByAuthorizedUsers),
        activityContext = activityContext
    )

    override suspend fun startManualGetCredentialFlow(
        activityContext: Context
    ): Result<String, CredentialService.CredentialError> = getCredential(
        request = buildSignInWithGoogleOptionRequest(),
        activityContext = activityContext
    )

    private suspend fun getCredential(
        request: GetCredentialRequest,
        activityContext: Context
    ): Result<String, CredentialService.CredentialError> = withContext(Dispatchers.IO) {
        try {
            val response = credentialManager.getCredential(activityContext, request)
            val idTokenCredential = when (val credential = response.credential) {
                is CustomCredential -> {
                    if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)
                        throw UnexpectedCredentialTypeThrowable()

                    GoogleIdTokenCredential.createFrom(credential.data)
                }

                else -> throw UnexpectedCredentialTypeThrowable()
            }
            Result.Success(idTokenCredential.idToken)
        } catch (e: NoCredentialException) {
            logE(e)
            Result.Error(
                CredentialService.CredentialError.NO_AUTHORIZED_CREDENTIAL,
                UiText.StringResource(R.string.error_sign_in_failed, true)
            )
        } catch (e: GetCredentialException) {
            logE(e)
            Result.Error(
                CredentialService.CredentialError.CREDENTIAL_PROCESS_FAILED,
                UiText.StringResource(R.string.error_sign_in_failed, true)
            )
        } catch (e: GoogleIdTokenParsingException) {
            logE(e)
            Result.Error(
                CredentialService.CredentialError.CREDENTIAL_PROCESS_FAILED,
                UiText.StringResource(R.string.error_sign_in_failed, true)
            )
        } catch (t: UnexpectedCredentialTypeThrowable) {
            logE(t)
            Result.Error(
                CredentialService.CredentialError.CREDENTIAL_PROCESS_FAILED,
                UiText.StringResource(R.string.error_sign_in_failed, true)
            )
        } catch (t: Throwable) {
            t.rethrowIfCoroutineCancellation()
            logE(t)
            Result.Error(
                CredentialService.CredentialError.CREDENTIAL_PROCESS_FAILED,
                UiText.StringResource(R.string.error_sign_in_failed, true)
            )
        }
    }

    override suspend fun clearCredentials() = withContext(Dispatchers.IO) {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    private fun buildGoogleIdOptionRequest(
        filterByAuthorizedAccounts: Boolean
    ): GetCredentialRequest {
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setAutoSelectEnabled(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()
        return GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()
    }

    private fun buildSignInWithGoogleOptionRequest(): GetCredentialRequest {
        val option = GetSignInWithGoogleOption
            .Builder(context.getString(R.string.default_web_client_id))
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()
    }
}

class UnexpectedCredentialTypeThrowable : Throwable()