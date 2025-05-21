package dev.ridill.oar.core.ui.navigation.destinations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import dev.ridill.oar.core.ui.components.scaleInWithFadeIn
import dev.ridill.oar.core.ui.components.scaleOutWithFadeOut
import dev.ridill.oar.core.ui.components.slideInHorizontallyWithFadeIn
import dev.ridill.oar.core.ui.components.slideOutHorizontallyWithFadeOut

sealed interface ScreenSpec : NavDestination {
    val arguments: List<NamedNavArgument>
        get() = emptyList()

    val deepLinks: List<NavDeepLink>
        get() = emptyList()

    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { slideInHorizontallyWithFadeIn { it / 2 } }

    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { scaleOutWithFadeOut() }

    val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
        get() = { scaleInWithFadeIn() }

    val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
        get() = { slideOutHorizontallyWithFadeOut { it / 2 } }

    @Composable
    fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    )
}