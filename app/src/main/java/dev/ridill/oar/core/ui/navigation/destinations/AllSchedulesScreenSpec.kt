package dev.ridill.oar.core.ui.navigation.destinations

import android.Manifest
import android.net.Uri
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
import androidx.paging.compose.collectAsLazyPagingItems
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.util.BuildUtil
import dev.ridill.oar.core.ui.components.CollectFlowEffect
import dev.ridill.oar.core.ui.components.OnLifecycleStartEffect
import dev.ridill.oar.core.ui.components.rememberPermissionState
import dev.ridill.oar.core.ui.components.rememberSnackbarController
import dev.ridill.oar.core.ui.util.launchAppNotificationSettings
import dev.ridill.oar.schedules.presentation.allSchedules.AllSchedulesScreen
import dev.ridill.oar.schedules.presentation.allSchedules.AllSchedulesViewModel

data object AllSchedulesScreenSpec : ScreenSpec {
    override val route: String
        get() = "all_schedules"

    override val labelRes: Int
        get() = R.string.destination_all_schedules

    override val deepLinks: List<NavDeepLink>
        get() = listOf(
            navDeepLink {
                uriPattern =
                    DEEPLINK_URI_PATTERN
            }
        )

    fun buildDeeplink(): Uri = DEEPLINK_URI_PATTERN.toUri()

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: AllSchedulesViewModel = hiltViewModel(navBackStackEntry)
        val allSchedulesPagingItems = viewModel.schedulesPagingData.collectAsLazyPagingItems()
        val state by viewModel.state.collectAsStateWithLifecycle()

        val snackbarController = rememberSnackbarController()
        val context = LocalContext.current

        val notificationPermissionState = if (BuildUtil.isNotificationRuntimePermissionNeeded())
            rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        else null

        CollectFlowEffect(
            flow = viewModel.events,
            snackbarController,
            context
        ) { event ->
            when (event) {
                is AllSchedulesViewModel.AllSchedulesEvent.ShowUiMessage -> {
                    snackbarController.showSnackbar(event.uiText.asString(context))
                }

                AllSchedulesViewModel.AllSchedulesEvent.RequestNotificationPermission -> {
                    context.launchAppNotificationSettings()
                }
            }
        }

        OnLifecycleStartEffect(
            viewModel,
            block = viewModel::refreshCurrentDate
        )

        AllSchedulesScreen(
            context = context,
            snackbarController = snackbarController,
            notificationPermissionState = notificationPermissionState,
            allSchedulesPagingItems = allSchedulesPagingItems,
            state = state,
            actions = viewModel,
            navigateUp = navController::navigateUp,
            navigateToAddEditSchedule = {
                navController.navigate(
                    AddEditTransactionScreenSpec.routeWithArg(
                        transactionId = it,
                        isScheduleTxMode = true
                    )
                )
            }
        )
    }
}

private const val DEEPLINK_URI_PATTERN =
    "${NavDestination.DEEP_LINK_URI}/schedules_list"