package dev.ridill.oar.core.ui.navigation.destinations

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.navDeepLink
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.CollectFlowEffect
import dev.ridill.oar.core.ui.components.NavigationResultEffect
import dev.ridill.oar.core.ui.components.rememberSnackbarController
import dev.ridill.oar.settings.presentation.backupEncryption.ACTION_ENCRYPTION_PASSWORD
import dev.ridill.oar.settings.presentation.backupSettings.BackupSettingsScreen
import dev.ridill.oar.settings.presentation.backupSettings.BackupSettingsViewModel

data object BackupSettingsScreenSpec : ScreenSpec {
    override val route: String
        get() = "backup_settings"

    override val labelRes: Int
        get() = R.string.destination_backup_settings

    override val deepLinks: List<NavDeepLink>
        get() = listOf(
            navDeepLink { uriPattern = DEEPLINK_URI_PATTERN }
        )

    fun buildDeeplink(): Uri = DEEPLINK_URI_PATTERN.toUri()

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: BackupSettingsViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()

        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current

        NavigationResultEffect<String>(
            resultKey = ACTION_ENCRYPTION_PASSWORD,
            navBackStackEntry = navBackStackEntry,
            viewModel,
            snackbarController,
            context,
            onResult = viewModel::onDestinationResult
        )

        val authorizationResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
                result.data?.let(viewModel::onAuthorizationResult)
            }
        )

        CollectFlowEffect(viewModel.events, snackbarController, context) { event ->
            when (event) {
                is BackupSettingsViewModel.BackupSettingsEvent.ShowUiMessage -> {
                    snackbarController.showSnackbar(
                        event.uiText.asString(context),
                        event.uiText.isErrorText
                    )
                }

                is BackupSettingsViewModel.BackupSettingsEvent.NavigateToBackupEncryptionScreen -> {
                    navController.navigate(BackupEncryptionScreenSpec.route)
                }

                is BackupSettingsViewModel.BackupSettingsEvent.StartAuthorizationFlow -> {
                    authorizationResultLauncher.launch(
                        IntentSenderRequest.Builder(event.pendingIntent).build()
                    )
                }
            }
        }

        BackupSettingsScreen(
            context = context,
            snackbarController = snackbarController,
            state = state,
            actions = viewModel,
            navigateUp = navController::navigateUp
        )
    }
}

private const val DEEPLINK_URI_PATTERN =
    "${NavDestination.DEEP_LINK_URI}/backup_settings"