package dev.ridill.oar.transactions.domain.autoDetection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TransactionSmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var service: TransactionAutoDetectService

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            .ifEmpty { return }
            .toList()

        service.detectTransactionsFromMessages(smsMessages)
    }
}