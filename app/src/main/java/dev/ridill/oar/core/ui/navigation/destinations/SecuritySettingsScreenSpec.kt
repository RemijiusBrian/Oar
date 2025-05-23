package dev.ridill.oar.core.ui.navigation.destinations

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.util.BiometricUtil
import dev.ridill.oar.core.domain.util.BuildUtil
import dev.ridill.oar.core.ui.components.rememberSnackbarController
import dev.ridill.oar.core.ui.util.launchAppNotificationSettings
import dev.ridill.oar.settings.presentation.securitySettings.SecuritySettingsScreen
import dev.ridill.oar.settings.presentation.securitySettings.SecuritySettingsViewModel

data object SecuritySettingsScreenSpec : ScreenSpec {
    override val route: String
        get() = "security_settings"

    override val labelRes: Int
        get() = R.string.destination_security_settings

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: SecuritySettingsViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()

        val context = LocalContext.current
        val snackbarController = rememberSnackbarController()

        val biometricManager = remember(context) { BiometricManager.from(context) }
        val biometricEnrollLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK) {
                    if (biometricManager.canAuthenticate(BiometricUtil.DefaultBiometricAuthenticators) == BiometricManager.BIOMETRIC_SUCCESS) {
                        BiometricUtil.startBiometricAuthentication(
                            context = context,
                            onAuthSuccess = viewModel::onAuthenticationSuccess
                        )
                    }
                }
            }
        )

        val notificationPermissionLauncher = if (BuildUtil.isNotificationRuntimePermissionNeeded())
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = viewModel::onNotificationPermissionResult
            ) else null

        LaunchedEffect(snackbarController, context) {
            viewModel.events.collect { event ->
                when (event) {
                    SecuritySettingsViewModel.SecuritySettingsEvent.LaunchBiometricAuthentication -> {
                        when (biometricManager.canAuthenticate(BiometricUtil.DefaultBiometricAuthenticators)) {
                            BiometricManager.BIOMETRIC_SUCCESS -> {
                                BiometricUtil.startBiometricAuthentication(
                                    context = context,
                                    onAuthSuccess = viewModel::onAuthenticationSuccess
                                )
                            }

                            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                                snackbarController.showSnackbar(
                                    context.getString(R.string.error_biometric_hw_unavailable)
                                )
                            }

                            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                                if (BuildUtil.isApiLevelAtLeast30) {
                                    // Prompts the user to create credentials that your app accepts.
                                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
                                        .apply {
                                            putExtra(
                                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                                BiometricUtil.DefaultBiometricAuthenticators
                                            )
                                        }
                                    biometricEnrollLauncher.launch(enrollIntent)
                                }
                            }

                            else -> Unit
                        }
                    }

                    SecuritySettingsViewModel.SecuritySettingsEvent.CheckNotificationPermission -> {
                        if (BuildUtil.isNotificationRuntimePermissionNeeded()) {
                            notificationPermissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }

                    SecuritySettingsViewModel.SecuritySettingsEvent.NavigateToNotificationSettings -> {
                        context.launchAppNotificationSettings()
                    }
                }
            }
        }

        SecuritySettingsScreen(
            snackbarController = snackbarController,
            state = state,
            actions = viewModel,
            navigateUp = navController::navigateUp,
        )
    }
}