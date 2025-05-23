package dev.ridill.oar.core.ui.navigation.bottomSheetDestination

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavDestinationDsl
import androidx.navigation.NavType
import kotlin.reflect.KClass
import kotlin.reflect.KType

@NavDestinationDsl
class BottomSheetNavigationDestinationBuilder :
    NavDestinationBuilder<OarBottomSheetNavigator.Destination> {

    private val bottomSheetNavigator: OarBottomSheetNavigator
    private val content: @Composable (NavBackStackEntry) -> Unit

    constructor(
        navigator: OarBottomSheetNavigator,
        route: String,
        content: @Composable (NavBackStackEntry) -> Unit
    ) : super(navigator, route) {
        this.bottomSheetNavigator = navigator
        this.content = content
    }

    public constructor(
        navigator: OarBottomSheetNavigator,
        route: KClass<*>,
        typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
        content: @Composable (NavBackStackEntry) -> Unit
    ) : super(navigator, route, typeMap) {
        this.bottomSheetNavigator = navigator
        this.content = content
    }

    override fun instantiateDestination(): OarBottomSheetNavigator.Destination =
        OarBottomSheetNavigator.Destination(bottomSheetNavigator, content)
}