package dev.ridill.oar.core.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.ridill.oar.core.ui.navigation.destinations.BottomSheetSpec
import dev.ridill.oar.core.ui.navigation.destinations.DashboardScreenSpec
import dev.ridill.oar.core.ui.navigation.destinations.NavDestination
import dev.ridill.oar.core.ui.navigation.destinations.NavGraphSpec
import dev.ridill.oar.core.ui.navigation.destinations.ScreenSpec

@Composable
fun OarNavHost(
    windowSizeClass: WindowSizeClass,
    bottomSheetNavigator: BottomSheetNavigator,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ModalBottomSheetLayout(bottomSheetNavigator) {
            NavHost(
                navController = navController,
                startDestination = DashboardScreenSpec.route,
                modifier = modifier
            ) {
                require(NavDestination.allDestinations.isNotEmpty()) {
                    "NavGraph must contain at least 1 child destination"
                }

                NavDestination.allDestinations.forEach { destination ->
                    when (destination) {
                        is NavGraphSpec -> addGraphSpec(
                            windowSizeClass = windowSizeClass,
                            navGraphSpec = destination,
                            navController = navController
                        )

                        is ScreenSpec -> addScreenSpec(
                            windowSizeClass = windowSizeClass,
                            screenSpec = destination,
                            navController = navController
                        )

                        is BottomSheetSpec -> addBottomSheetSpec(
                            windowSizeClass = windowSizeClass,
                            sheetSpec = destination,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

private fun NavGraphBuilder.addScreenSpec(
    windowSizeClass: WindowSizeClass,
    screenSpec: ScreenSpec,
    navController: NavHostController
) {
    composable(
        route = screenSpec.route,
        arguments = screenSpec.arguments,
        deepLinks = screenSpec.deepLinks,
        enterTransition = screenSpec.enterTransition,
        exitTransition = screenSpec.exitTransition,
        popEnterTransition = screenSpec.popEnterTransition,
        popExitTransition = screenSpec.popExitTransition
    ) { navBackStackEntry ->
        screenSpec.Content(
            windowSizeClass = windowSizeClass,
            navController = navController,
            navBackStackEntry = navBackStackEntry
        )
    }
}

private fun NavGraphBuilder.addBottomSheetSpec(
    windowSizeClass: WindowSizeClass,
    sheetSpec: BottomSheetSpec,
    navController: NavHostController
) {
    bottomSheet(
        route = sheetSpec.route,
        arguments = sheetSpec.arguments,
        deepLinks = sheetSpec.deepLinks,
    ) { navBackStackEntry ->
        sheetSpec.Content(
            windowSizeClass = windowSizeClass,
            navController = navController,
            navBackStackEntry = navBackStackEntry
        )
    }
}

private fun NavGraphBuilder.addGraphSpec(
    windowSizeClass: WindowSizeClass,
    navGraphSpec: NavGraphSpec,
    navController: NavHostController
) {
    require(navGraphSpec.children.isNotEmpty()) {
        "NavGraph must contain at least 1 child destination"
    }
    navigation(
        startDestination = navGraphSpec.startDestination.route,
        route = navGraphSpec.route
    ) {
        navGraphSpec.children.forEach { destination ->
            when (destination) {
                is ScreenSpec -> addScreenSpec(
                    windowSizeClass = windowSizeClass,
                    screenSpec = destination,
                    navController = navController
                )

                is NavGraphSpec -> addGraphSpec(
                    windowSizeClass = windowSizeClass,
                    navGraphSpec = navGraphSpec,
                    navController = navController
                )

                is BottomSheetSpec -> addBottomSheetSpec(
                    windowSizeClass = windowSizeClass,
                    sheetSpec = destination,
                    navController = navController
                )
            }
        }
    }
}