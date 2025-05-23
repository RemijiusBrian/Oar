package dev.ridill.oar.core.domain.util

import java.util.Currency
import java.util.Locale

object LocaleUtil {
    val defaultLocale: Locale
        get() = Locale.getDefault()

    val defaultCurrency: Currency
        get() = Currency.getInstance(defaultLocale)

    val availableLocales: List<Locale>
        get() = Locale.getAvailableLocales()
            .toList()

    fun currencyForCode(code: String): Currency = Currency.getInstance(code)
}