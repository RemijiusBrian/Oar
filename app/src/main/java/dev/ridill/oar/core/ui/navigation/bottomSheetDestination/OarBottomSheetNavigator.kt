package dev.ridill.oar.core.ui.navigation.bottomSheetDestination

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.FloatingWindow
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.LocalOwnersProvider
import androidx.navigation.get
import dev.ridill.oar.core.ui.components.OarModalBottomSheet
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.transform
import kotlin.coroutines.cancellation.CancellationException

@Navigator.Name("bottomSheet")
class OarBottomSheetNavigator(
    private val sheetState: SheetState
) : Navigator<OarBottomSheetNavigator.Destination>() {

    val backStack
        get() = state.backStack

    val transitionInProgress
        get() = state.transitionsInProgress

    fun dismiss(navBackStackEntry: NavBackStackEntry) {
        popBackStack(navBackStackEntry, false)
    }

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        entries.forEach { entry -> state.push(entry) }
    }

    override fun createDestination(): Destination = Destination(this) {}

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        state.popWithTransition(popUpTo, savedState)
        // When popping, the incoming dialog is marked transitioning to hold it in
        // STARTED. With pop complete, we can remove it from transition so it can move to RESUMED.
        val popIndex = state.transitionsInProgress.value.indexOf(popUpTo)
        // do not mark complete for entries up to and including popUpTo
        state.transitionsInProgress.value.forEachIndexed { index, entry ->
            if (index > popIndex) onTransitionComplete(entry)
        }
    }

    fun onTransitionComplete(navBackStackEntry: NavBackStackEntry) {
        state.markTransitionComplete(navBackStackEntry)
    }

    class Destination(
        navigator: OarBottomSheetNavigator,
        val content: @Composable (NavBackStackEntry) -> Unit
    ) : NavDestination(navigator), FloatingWindow

    val sheetContent: @Composable ColumnScope.() -> Unit = {
        val saveableStateHolder = rememberSaveableStateHolder()
        val transitionsInProgressEntries by transitionInProgress.collectAsState()

        // The latest back stack entry, retained until the sheet is completely hidden
        // While the back stack is updated immediately, we might still be hiding the sheet, so
        // we keep the entry around until the sheet is hidden
        val retainedEntry by produceState<NavBackStackEntry?>(
            initialValue = null,
            key1 = backStack
        ) {
            backStack
                .transform { backStackEntries ->
                    // Always hide the sheet when the back stack is updated
                    // Regardless of whether we're popping or pushing, we always want to hide
                    // the sheet first before deciding whether to re-show it or keep it hidden
                    try {
                        sheetState.hide()
                    } catch (_: CancellationException) {
                        // We catch but ignore possible cancellation exceptions as we don't want
                        // them to bubble up and cancel the whole produceState coroutine
                    } finally {
                        emit(backStackEntries.lastOrNull())
                    }
                }
                .collect {
                    value = it
                }
        }

        if (retainedEntry != null) {
            LaunchedEffect(retainedEntry) {
                sheetState.show()
            }

            BackHandler {
                state.popWithTransition(popUpTo = retainedEntry!!, saveState = false)
            }
        }

        SheetContentHost(
            backStackEntry = retainedEntry,
            sheetState = sheetState,
            saveableStateHolder = saveableStateHolder,
            onSheetShown = {
                transitionsInProgressEntries.forEach(state::markTransitionComplete)
            },
            onSheetDismissed = { backStackEntry ->
                // Sheet dismissal can be started through popBackStack in which case we have a
                // transition that we'll want to complete
                if (transitionsInProgressEntries.contains(backStackEntry)) {
                    state.markTransitionComplete(backStackEntry)
                }
                // If there is no transition in progress, the sheet has been dimissed by the
                // user (for example by tapping on the scrim or through an accessibility action)
                // In this case, we will immediately pop without a transition as the sheet has
                // already been hidden
                else {
                    state.pop(popUpTo = backStackEntry, saveState = false)
                }
            }
        )
    }
}

fun NavGraphBuilder.bottomSheet(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    destination(
        BottomSheetNavigationDestinationBuilder(
            provider[OarBottomSheetNavigator::class],
            route,
            content
        )
            .apply {
                arguments.forEach { (argumentName, argument) -> argument(argumentName, argument) }
                deepLinks.forEach { deepLink -> deepLink(deepLink) }
            }
    )
}

@Composable
fun SheetContentHost(
    backStackEntry: NavBackStackEntry?,
    sheetState: SheetState,
    saveableStateHolder: SaveableStateHolder,
    onSheetShown: (entry: NavBackStackEntry) -> Unit,
    onSheetDismissed: (entry: NavBackStackEntry) -> Unit,
) {
    if (backStackEntry != null) {
        val currentOnSheetShown by rememberUpdatedState(onSheetShown)
        val currentOnSheetDismissed by rememberUpdatedState(onSheetDismissed)
        LaunchedEffect(sheetState, backStackEntry) {
            snapshotFlow { sheetState.isVisible }
                // We are only interested in changes in the sheet's visibility
                .distinctUntilChanged()
                // distinctUntilChanged emits the initial value which we don't need
                .drop(1)
                .collect { visible ->
                    if (visible) {
                        currentOnSheetShown(backStackEntry)
                    } else {
                        currentOnSheetDismissed(backStackEntry)
                    }
                }
        }
        backStackEntry.LocalOwnersProvider(saveableStateHolder) {
            val content =
                (backStackEntry.destination as OarBottomSheetNavigator.Destination).content

            OarModalBottomSheet(
                onDismissRequest = {}
            ) {
                content(backStackEntry)
            }
        }
    }
}