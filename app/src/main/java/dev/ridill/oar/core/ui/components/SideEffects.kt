package dev.ridill.oar.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OnLifecycleEventEffect(
    lifecycleEvent: Lifecycle.Event,
    vararg keys: Any?,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    block: () -> Unit
) {
    val observer = remember(lifecycleOwner, *keys) {
        LifecycleEventObserver { _, event ->
            if (event == lifecycleEvent) {
                block()
            }
        }
    }
    val lifecycle = lifecycleOwner.lifecycle
    DisposableEffect(lifecycle, *keys) {
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun OnLifecycleStartEffect(
    vararg keys: Any?,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    block: () -> Unit
) = OnLifecycleEventEffect(
    lifecycleEvent = Lifecycle.Event.ON_START,
    keys = keys,
    lifecycleOwner = lifecycleOwner,
    block = block
)

@Composable
fun OnLifecycleResumeEffect(
    vararg keys: Any?,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    block: () -> Unit
) = OnLifecycleEventEffect(
    lifecycleEvent = Lifecycle.Event.ON_RESUME,
    keys = keys,
    lifecycleOwner = lifecycleOwner,
    block = block
)

@Composable
fun <T> CollectFlowEffect(
    flow: Flow<T>,
    vararg keys: Any?,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onCollect: suspend (T) -> Unit
) {
    val updatedFlow by rememberUpdatedState(newValue = flow)
    LaunchedEffect(updatedFlow, lifecycleOwner, *keys) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            updatedFlow.collectLatest { onCollect(it) }
        }
    }
}