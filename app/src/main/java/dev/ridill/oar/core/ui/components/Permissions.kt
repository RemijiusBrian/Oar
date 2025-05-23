package dev.ridill.oar.core.ui.components

import android.app.Activity
import android.content.Context
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
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

data class PermissionState(
    val permission: String,
    private val context: Context,
    private val activity: Activity
) {
    var status: PermissionStatus by mutableStateOf(getPermissionStatus())

    var launcher: ActivityResultLauncher<String>? = null

    val isGranted: Boolean
        get() = status.isGranted

    val shouldShowRationale: Boolean
        get() = status.shouldShowRationale

    fun launchRequest() = launcher?.launch(permission)

    fun refreshPermissionStatus() {
        status = getPermissionStatus()
    }

    private fun getPermissionStatus(): PermissionStatus {
        val hasPermission = context.isPermissionGranted(permission)
        return if (hasPermission) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(
                activity.shouldShowPermissionRationale(permission)
            )
        }
    }
}

sealed interface PermissionStatus {
    object Granted : PermissionStatus
    data class Denied(
        val shouldShowRationale: Boolean
    ) : PermissionStatus
}

private val PermissionStatus.isGranted: Boolean
    get() = this == PermissionStatus.Granted

private val PermissionStatus.shouldShowRationale: Boolean
    get() = when (this) {
        PermissionStatus.Granted -> false
        is PermissionStatus.Denied -> shouldShowRationale
    }

@Composable
fun rememberPermissionState(
    permission: String,
    context: Context = LocalContext.current,
    activity: Activity = LocalActivity.current ?: error("Unable to get Activity"),
    onPermissionResult: (Boolean) -> Unit = {}
): PermissionState {
    val permissionState = remember(permission, context, activity) {
        PermissionState(
            permission = permission,
            context = context,
            activity = activity
        )
    }

    OnLifecycleResumeEffect {
        if (permissionState.status != PermissionStatus.Granted) {
            permissionState.refreshPermissionStatus()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
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