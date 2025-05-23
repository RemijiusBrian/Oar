package dev.ridill.oar.core.ui.components

import android.app.Activity
import android.content.Context
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import dev.ridill.oar.core.ui.util.isPermissionGranted
import dev.ridill.oar.core.ui.util.shouldShowPermissionRationale

data class MultiplePermissionsState(
    val permissions: List<String>,
    private val context: Context,
    private val activity: Activity
) {
    var status: MultiplePermissionStatus by mutableStateOf(getPermissionStatus())

    val areAllPermissionsGranted: Boolean
        get() = permissions.all { context.isPermissionGranted(it) }

    var launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>? = null
    fun launchRequest() = launcher?.launch(permissions.toTypedArray())

    fun refreshPermissionStatus() {
        status = getPermissionStatus()
    }

    private fun getPermissionStatus(): MultiplePermissionStatus {
        val permissionsMap = permissions.associateWith { context.isPermissionGranted(it) }
        val areAllGranted = permissionsMap.values.all { it }
        return if (areAllGranted) {
            MultiplePermissionStatus.AllGranted
        } else {
            val deniedPermissions = permissionsMap.filterValues { !it }
            val shouldShowRationaleMap = deniedPermissions.mapValues {
                activity.shouldShowPermissionRationale(it.key)
            }
            MultiplePermissionStatus.Denied(
                shouldShowRationaleMap = shouldShowRationaleMap
            )
        }
    }
}

sealed interface MultiplePermissionStatus {
    object AllGranted : MultiplePermissionStatus
    data class Denied(
        val shouldShowRationaleMap: Map<String, Boolean>
    ) : MultiplePermissionStatus
}

@Composable
fun rememberMultiplePermissionsState(
    permissions: List<String>,
    context: Context = LocalContext.current,
    activity: Activity = LocalActivity.current ?: error("Unable to get Activity"),
    onPermissionResult: (Map<String, Boolean>) -> Unit = {}
): MultiplePermissionsState {
    val permissionState = remember(permissions, context, activity) {
        MultiplePermissionsState(
            permissions = permissions,
            context = context,
            activity = activity
        )
    }

    OnLifecycleResumeEffect {
        if (permissionState.status != MultiplePermissionStatus.AllGranted) {
            permissionState.refreshPermissionStatus()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            permissionState.refreshPermissionStatus()
            onPermissionResult(it)
        }
    )

    DisposableEffect(permissionState, launcher) {
        permissionState.launcher = launcher
        onDispose {
            permissionState.launcher = null
        }
    }

    return permissionState
}