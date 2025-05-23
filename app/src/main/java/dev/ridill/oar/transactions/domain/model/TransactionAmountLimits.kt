package dev.ridill.oar.transactions.domain.model

data class TransactionAmountLimits(
    val upperLimit: Double,
    val lowerLimit: Double
)