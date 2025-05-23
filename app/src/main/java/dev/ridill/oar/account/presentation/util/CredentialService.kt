package dev.ridill.oar.account.presentation.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.ridill.oar.core.domain.model.Error
import dev.ridill.oar.core.domain.model.Result

typealias IdToken = String

interface CredentialService {

    suspend fun startGetCredentialFlow(
        filterByAuthorizedUsers: Boolean,
        activityContext: Context
    ): Result<IdToken, CredentialError>

    suspend fun startManualGetCredentialFlow(
        activityContext: Context
    ): Result<IdToken, CredentialError>

    suspend fun clearCredentials()

    enum class CredentialError : Error {
        NO_AUTHORIZED_CREDENTIAL,
        CREDENTIAL_PROCESS_FAILED
    }
}

@Composable
fun rememberCredentialService(
    context: Context = LocalContext.current
): CredentialService = remember(context) {
    DefaultCredentialService(context)
}