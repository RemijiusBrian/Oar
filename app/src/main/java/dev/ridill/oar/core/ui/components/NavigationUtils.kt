package dev.ridill.oar.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

@Composable
fun <T> NavigationResultEffect(
    resultKey: String,
    navBackStackEntry: NavBackStackEntry,
    vararg keys: Any,
    onResult: (T) -> Unit
) {
    val result = navBackStackEntry
        .savedStateHandle
        .get<T>(resultKey)

    LaunchedEffect(result, navBackStackEntry, *keys) {
        result?.let(onResult)
        navBackStackEntry.savedStateHandle
            .remove<T>(resultKey)
    }
}

@Composable
fun <T> FloatingWindowNavigationResultEffect(
    resultKey: String,
    navBackStackEntry: NavBackStackEntry,
    vararg keys: Any,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onResult: (T) -> Unit
) = OnLifecycleResumeEffect(
    navBackStackEntry,
    *keys,
    lifecycleOwner = lifecycleOwner
) {
    navBackStackEntry
        .savedStateHandle
        .get<T>(resultKey)
        ?.let(onResult)

    navBackStackEntry.savedStateHandle
        .remove<T>(resultKey)
}

fun <T> NavHostController.navigateUpWithResult(
    key: String,
    result: T?,
    backStackEntry: NavBackStackEntry? = this.previousBackStackEntry
) {
    backStackEntry
        ?.savedStateHandle
        ?.set(key, result)
    navigateUp()
}