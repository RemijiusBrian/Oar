package dev.ridill.oar.core.ui.navigation.destinations

import android.Manifest
import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest.Builder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.oar.R
import dev.ridill.oar.account.presentation.util.rememberCredentialService
import dev.ridill.oar.application.RUN_CONFIG_RESTORE_EXTRA
import dev.ridill.oar.core.domain.util.BuildUtil
import dev.ridill.oar.core.ui.components.CollectFlowEffect
import dev.ridill.oar.core.ui.components.FloatingWindowNavigationResultEffect
import dev.ridill.oar.core.ui.components.rememberMultiplePermissionsState
import dev.ridill.oar.core.ui.components.rememberSnackbarController
import dev.ridill.oar.core.ui.util.restartApplication
import dev.ridill.oar.onboarding.domain.model.OnboardingPage
import dev.ridill.oar.onboarding.presentation.OnboardingScreen
import dev.ridill.oar.onboarding.presentation.OnboardingViewModel
import java.util.Currency

data object OnboardingScreenSpec : ScreenSpec {
    override val route: String
        get() = "onboarding"

    override val labelRes: Int
        get() = R.string.destination_onboarding

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: OnboardingViewModel = hiltViewModel(navBackStackEntry)
        val pagerState = rememberPagerState(
            pageCount = { OnboardingPage.entries.size }
        )
        val state by viewModel.state.collectAsStateWithLifecycle()
        val budgetInputState = viewModel.budgetInputState

        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current
        val activity = LocalActivity.current

        val permissionsState = rememberMultiplePermissionsState(
            permissions = getPermissionsList(),
            onPermissionResult = viewModel::onPermissionsRequestResult
        )

        val credentialService = rememberCredentialService(context)
        val currentPage by remember(pagerState) {
            derivedStateOf { pagerState.currentPage }
        }

        LaunchedEffect(currentPage) {
            viewModel.onPageChange(currentPage)
        }

        val authorizationResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
                result.data?.let(viewModel::onAuthorizationResult)
            }
        )

        FloatingWindowNavigationResultEffect<Currency>(
            resultKey = CurrencySelectionSheetSpec.SELECTED_CURRENCY,
            navBackStackEntry = navBackStackEntry,
            viewModel,
            snackbarController,
            context,
            onResult = viewModel::onCurrencySelected
        )

        CollectFlowEffect(viewModel.events, snackbarController, context) { event ->
            when (event) {
                is OnboardingViewModel.OnboardingEvent.NavigateToPage -> {
                    if (!pagerState.isScrollInProgress)
                        pagerState.animateScrollToPage(event.page.ordinal)
                }

                OnboardingViewModel.OnboardingEvent.LaunchNotificationPermissionRequest -> {
                    permissionsState.launchRequest()
                }

                is OnboardingViewModel.OnboardingEvent.ShowUiMessage -> {
                    snackbarController.showSnackbar(
                        event.uiText.asString(context),
                        event.uiText.isErrorText
                    )
                }

                OnboardingViewModel.OnboardingEvent.OnboardingConcluded -> {
                    navController.navigate(DashboardScreenSpec.route) {
                        popUpTo(route) {
                            inclusive = true
                        }
                    }
                }

                OnboardingViewModel.OnboardingEvent.RestartApplication -> {
                    context.restartApplication(
                        editIntent = {
                            putExtra(RUN_CONFIG_RESTORE_EXTRA, true)
                        }
                    )
                }

                is OnboardingViewModel.OnboardingEvent.StartAutoSignInFlow -> {
                    activity?.let {
                        val result = credentialService.startGetCredentialFlow(
                            filterByAuthorizedUsers = event.filterByAuthorizedAccounts,
                            activityContext = it
                        )
                        viewModel.onCredentialResult(result)
                    }
                }

                is OnboardingViewModel.OnboardingEvent.StartAuthorizationFlow -> {
                    authorizationResultLauncher.launch(
                        Builder(event.pendingIntent).build()
                    )
                }

                OnboardingViewModel.OnboardingEvent.StartManualSignInFlow -> {
                    activity?.let {
                        val result = credentialService.startManualGetCredentialFlow(it)
                        viewModel.onCredentialResult(result)
                    }
                }
            }
        }

        OnboardingScreen(
            snackbarController = snackbarController,
            pagerState = pagerState,
            permissionsState = permissionsState,
            state = state,
            budgetInputState = budgetInputState,
            navigateToCurrencySelection = {
                navController.navigate(
                    CurrencySelectionSheetSpec.routeWithArg(
                        preSelectedCurrencyCode = state.appCurrency.currencyCode
                    )
                )
            },
            actions = viewModel
        )
    }

    private fun getPermissionsList() = buildList {
        if (BuildUtil.isNotificationRuntimePermissionNeeded())
            add(Manifest.permission.POST_NOTIFICATIONS)

        if (BuildUtil.isScheduleAlarmRuntimePermissionRequired())
            add(Manifest.permission.SCHEDULE_EXACT_ALARM)
    }
}