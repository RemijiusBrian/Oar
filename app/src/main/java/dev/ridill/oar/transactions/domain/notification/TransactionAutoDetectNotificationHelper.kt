package dev.ridill.oar.transactions.domain.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import dev.ridill.oar.R
import dev.ridill.oar.application.OarActivity
import dev.ridill.oar.core.domain.notification.NotificationHelper
import dev.ridill.oar.core.domain.util.UtilConstants
import dev.ridill.oar.core.ui.navigation.destinations.ARG_TRANSACTION_ID
import dev.ridill.oar.core.ui.navigation.destinations.AddEditTransactionScreenSpec
import dev.ridill.oar.transactions.domain.model.Transaction
import dev.ridill.oar.transactions.domain.model.TransactionType

@SuppressLint("MissingPermission")
class TransactionAutoDetectNotificationHelper(
    private val context: Context
) : NotificationHelper<Transaction> {
    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        registerChannelGroup()
        registerChannel()
    }

    override val channelId: String
        get() = "${context.packageName}.NOTIFICATION_CHANNEL_TRANSACTION_AUTO_DETECT"

    private val groupId: String
        get() = "${context.packageName}.AUTO_DETECTED_TRANSACTIONS_NOTIFICATION_GROUP"

    override fun registerChannelGroup() {
        val group = NotificationChannelGroupCompat
            .Builder(NotificationHelper.Groups.transactions(context))
            .setName(context.getString(R.string.notification_channel_group_transactions_name))
            .build()
        notificationManager.createNotificationChannelGroup(group)
    }

    override fun registerChannel() {
        val channel = NotificationChannelCompat
            .Builder(channelId, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(context.getString(R.string.notification_channel_transaction_auto_detect_name))
            .setGroup(NotificationHelper.Groups.transactions(context))
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    override fun buildBaseNotification(): NotificationCompat.Builder =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setGroup(groupId)

    override fun postNotification(id: Int, data: Transaction) {
        if (!notificationManager.areNotificationsEnabled()) return

        val notification = buildBaseNotification()
            .setContentTitle(context.getString(R.string.new_transaction_detected))
            .setContentText(
                context.getString(
                    when (data.type) {
                        TransactionType.CREDIT -> R.string.amount_credited_notification_message
                        TransactionType.DEBIT -> R.string.amount_debited_notification_message
                    },
                    data.amount,
                    data.note
                )
            )
            .setContentIntent(buildContentIntent(data.id))
            .addAction(buildDeleteAction(data.id))
            .addAction(buildMarkExcludedAction(data.id))
            .build()

        notificationManager.notify(id, notification)
    }

    override fun updateNotification(id: Int, notification: Notification) {
        notificationManager.notify(id, notification)
    }

    override fun dismissNotification(id: Int) {
        notificationManager.cancel(id)
    }

    override fun dismissAllNotifications() {
        notificationManager.cancelAll()
    }

    private fun buildContentIntent(id: Long): PendingIntent? {
        val intent = Intent(
            Intent.ACTION_VIEW,
            AddEditTransactionScreenSpec.buildDeeplink(id),
            context,
            OarActivity::class.java
        )
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(id.hashCode(), UtilConstants.pendingIntentFlags)
        }
    }

    private fun buildDeleteAction(id: Long): NotificationCompat.Action {
        val intent = Intent(context, DeleteTransactionActionReceiver::class.java).apply {
            putExtra(ARG_TRANSACTION_ID, id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, id.hashCode(), intent, UtilConstants.pendingIntentFlags
        )

        return NotificationCompat.Action.Builder(
            R.drawable.ic_notification,
            context.getString(R.string.action_delete),
            pendingIntent
        ).build()
    }

    private fun buildMarkExcludedAction(id: Long): NotificationCompat.Action {
        val intent = Intent(context, MarkTransactionExcludedActionReceiver::class.java).apply {
            putExtra(ARG_TRANSACTION_ID, id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, id.hashCode(), intent, UtilConstants.pendingIntentFlags
        )

        return NotificationCompat.Action.Builder(
            R.drawable.ic_notification,
            context.getString(R.string.action_mark_excluded),
            pendingIntent
        ).build()
    }
}