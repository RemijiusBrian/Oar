package dev.ridill.oar.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import dev.ridill.oar.di.ApplicationScope
import dev.ridill.oar.settings.domain.appInit.AppInitWorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimeSetReceiver : BroadcastReceiver() {

    @ApplicationScope
    @Inject
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var appInitManager: AppInitWorkManager

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Intent.ACTION_TIME_CHANGED) return
        applicationScope.launch {
            appInitManager.startAlarmsAndReminderInitWorkers()
        }
    }
}