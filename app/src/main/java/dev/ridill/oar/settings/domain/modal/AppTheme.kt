package dev.ridill.oar.settings.domain.modal

import androidx.annotation.StringRes
import dev.ridill.oar.R

enum class AppTheme(@StringRes override val labelRes: Int) : BaseRadioOption {
    SYSTEM_DEFAULT(R.string.app_theme_system_default),
    LIGHT(R.string.app_theme_light),
    DARK(R.string.app_theme_dark)
}