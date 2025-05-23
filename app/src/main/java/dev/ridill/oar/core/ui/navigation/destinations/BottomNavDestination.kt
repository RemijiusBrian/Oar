package dev.ridill.oar.core.ui.navigation.destinations

import androidx.annotation.DrawableRes

sealed interface BottomNavDestination : NavDestination {
    companion object {
        val bottomNavDestinations: List<BottomNavDestination>
            get() = NavDestination.allDestinations
                .filterIsInstance<BottomNavDestination>()
                .sortedBy { it.precedence }
    }

    @get:DrawableRes
    val iconRes: Int

    val precedence: Int
        get() = 0
}