package dev.ridill.oar.core.ui.util

import androidx.compose.runtime.compositionLocalOf
import dev.ridill.oar.core.domain.util.LocaleUtil

val LocalCurrencyPreference = compositionLocalOf { LocaleUtil.defaultCurrency }