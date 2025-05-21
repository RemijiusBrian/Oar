package dev.ridill.oar.core.domain.remoteConfig

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RemoteConfig(
    val sourceCodeUrl: String,
    val transactionAutoDetectFeatureEnabled: Boolean,
    val deleteAccountFeatureEnabled: Boolean,
    val autoDetectTransactionRegexPatterns: AutoDetectTransactionRegexPatterns?
)

data class AutoDetectTransactionRegexPatterns(
    @Expose
    @SerializedName("originating_address")
    val originatingAddress: String,
    @Expose
    @SerializedName("credit")
    val credit: String,
    @Expose
    @SerializedName("debit")
    val debit: String,
    @Expose
    @SerializedName("timestamp")
    val timestamp: String,
    @Expose
    @SerializedName("second_party_start")
    val secondPartyStart: String,
    @Expose
    @SerializedName("second_party_end")
    val secondPartyEnd: String,
    @Expose
    @SerializedName("misc_payment")
    val miscPayment: String,
)