package dev.ridill.oar.core.domain.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.auth.AuthPromptCallback
import androidx.biometric.auth.startClass2BiometricOrCredentialAuthentication
import androidx.fragment.app.FragmentActivity
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.util.findActivity

object BiometricUtil {
    val DefaultBiometricAuthenticators: Int
        get() = BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL

    inline fun startBiometricAuthentication(
        context: Context,
        crossinline onAuthSuccess: () -> Unit
    ) {
        val activity = context.findActivity() as AppCompatActivity
        val title = context.getString(
            R.string.biometric_prompt_title_app_name,
            context.getString(R.string.app_name)
        )
        val subtitle = context.getString(R.string.biometric_or_screen_lock_prompt_message)
        val authPromptCallback = object : AuthPromptCallback() {
            override fun onAuthenticationSucceeded(
                activity: FragmentActivity?,
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(activity, result)
                onAuthSuccess()
            }
        }

        activity.startClass2BiometricOrCredentialAuthentication(
            title = title,
            subtitle = subtitle,
            callback = authPromptCallback
        )
    }
}
