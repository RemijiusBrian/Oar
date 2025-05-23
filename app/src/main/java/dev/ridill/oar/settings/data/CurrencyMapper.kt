package dev.ridill.oar.settings.data

import dev.ridill.oar.settings.data.local.entity.CurrencyListEntity
import java.util.Currency

fun Currency.toEntity(): CurrencyListEntity = CurrencyListEntity(
    currencyCode = currencyCode,
    displayName = displayName
)