package dev.ridill.oar.settings.domain.modal

import androidx.annotation.StringRes

interface BaseRadioOption {
    @get:StringRes
    val labelRes: Int
}