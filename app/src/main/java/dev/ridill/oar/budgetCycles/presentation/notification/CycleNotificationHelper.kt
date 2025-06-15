package dev.ridill.oar.budgetCycles.presentation.notification

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.ridill.oar.R
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleEntry
import dev.ridill.oar.core.domain.notification.NotificationHelper

@SuppressLint("MissingPermission")
class CycleNotificationHelper(
    private val context: Context
) : NotificationHelper<BudgetCycleEntry> {

    private val notificationManager = NotificationManagerCompat.from(context)

    override val channelId: String
        get() = "${context.packageName}.NOTIFICATION_CHANNEL_BUDGET_CYCLES"

    init {
        registerChannelGroup()
        registerChannel()
    }

    override fun registerChannelGroup() {
        val group = NotificationChannelGroupCompat
            .Builder(NotificationHelper.Groups.others(context))
            .setName(context.getString(R.string.notification_channel_group_others_name))
            .build()
        notificationManager.createNotificationChannelGroup(group)
    }

    override fun registerChannel() {
        val channel = NotificationChannelCompat
            .Builder(channelId, NotificationManagerCompat.IMPORTANCE_LOW)
            .setName(context.getString(R.string.notification_channel_budget_cycle_name))
            .setGroup(NotificationHelper.Groups.others(context))
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    override fun buildBaseNotification(): NotificationCompat.Builder =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
//            .setContentIntent(buildContentIntent())
//            .setGroup(summaryId)

    override fun postNotification(
        id: Int,
        builder: NotificationCompat.Builder.() -> NotificationCompat.Builder
    ) {
        val notification = builder(buildBaseNotification())
            .build()
        with(notificationManager) {
            notify(id, notification)
        }
    }
}