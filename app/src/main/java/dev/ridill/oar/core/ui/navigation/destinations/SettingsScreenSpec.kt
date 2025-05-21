package dev.ridill.oar.core.ui.navigation.destinations

import android.Manifest
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.oar.R
import dev.ridill.oar.account.presentation.util.rememberCredentialService
import dev.ridill.oar.core.ui.components.CollectFlowEffect
import dev.ridill.oar.core.ui.components.FloatingWindowNavigationResultEffect
import dev.ridill.oar.core.ui.components.rememberPermissionState
import dev.ridill.oar.core.ui.components.rememberSnackbarController
import dev.ridill.oar.core.ui.util.LocalCurrencyPreference
import dev.ridill.oar.core.ui.util.UiText
import dev.ridill.oar.core.ui.util.launchAppNotificationSettings
import dev.ridill.oar.core.ui.util.launchAppSettings
import dev.ridill.oar.settings.presentation.settings.SettingsScreen
import dev.ridill.oar.settings.presentation.settings.SettingsViewModel
import java.util.Currency

data object SettingsScreenSpec : ScreenSpec {

    override val route: String
        get() = "settings"

    override val labelRes: Int
        get() = R.string.destination_settings

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: SettingsViewModel = hiltViewModel(navBackStackEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()
        val currencyPreference = LocalCurrencyPreference.current

        val smsPermissionState = rememberPermissionState(
            permission = Manifest.permission.RECEIVE_SMS,
            onPermissionResult = viewModel::onSmsPermissionResult
        )

        val context = LocalContext.current
        val snackbarController = rememberSnackbarController()
        val credentialService = rememberCredentialService(context = context)
        val activity = LocalActivity.current
        CollectFlowEffect(viewModel.events, snackbarController, context, credentialService) { event ->
            when (event) {
                is SettingsViewModel.SettingsEvent.ShowUiMessage -> {
                    snackbarController.showSnackbar(
                        event.uiText.asString(context),
                        event.uiText.isErrorText
                    )
                }

                SettingsViewModel.SettingsEvent.RequestSMSPermission -> {
                    smsPermissionState.launchRequest()
                }

                SettingsViewModel.SettingsEvent.LaunchAppSettings -> {
                    context.launchAppSettings()
                }

                SettingsViewModel.SettingsEvent.StartManualSignInFlow -> {
                    activity?.let {
                        val result = credentialService.startManualGetCredentialFlow(
                            activityContext = it
                        )
                        viewModel.onCredentialResult(result)
                    }
                }
            }
        }

        FloatingWindowNavigationResultEffect<String>(
            resultKey = UpdateBudgetSheetSpec.UPDATE_BUDGET_RESULT,
            navBackStackEntry = navBackStackEntry,
            viewModel,
        ) { result ->
            when (result) {
                UpdateBudgetSheetSpec.RESULT_BUDGET_UPDATED -> {
                    snackbarController.showSnackbar(
                        UiText.StringResource(R.string.budget_updated).asString(context)
                    )
                }
            }
        }

        FloatingWindowNavigationResultEffect<Currency>(
            resultKey = CurrencySelectionSheetSpec.SELECTED_CURRENCY,
            navBackStackEntry = navBackStackEntry,
            viewModel,
            snackbarController,
            context,
            onResult = viewModel::onBaseCurrencySelected
        )

        SettingsScreen(
            snackbarController = snackbarController,
            currencyPreference = currencyPreference,
            state = state,
            actions = viewModel,
            navigateUp = navController::navigateUp,
            navigateToNotificationSettings = context::launchAppNotificationSettings,
            navigateToUpdateBudget = { navController.navigate(UpdateBudgetSheetSpec.route) },
            navigateToCurrencySelection = {
                navController.navigate(
                    CurrencySelectionSheetSpec.routeWithArg(
                        preSelectedCurrencyCode = currencyPreference.currencyCode
                    )
                )
            },
            navigateToManageTags = { navController.navigate(TagsGraphSpec.route) },
            navigateToBackupSettings = { navController.navigate(BackupSettingsScreenSpec.route) },
            navigateToSecuritySettings = { navController.navigate(SecuritySettingsScreenSpec.route) },
            launchUriInBrowser = {
                val intent = Intent(Intent.ACTION_VIEW, it)
                context.startActivity(intent)
            }
        )
    }
}