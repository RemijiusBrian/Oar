package dev.ridill.oar.core.data.preferences.animPreferences

import dev.ridill.oar.core.domain.model.AnimPreferences
import kotlinx.coroutines.flow.Flow

interface AnimPreferencesManager {
    companion object {
        const val NAME = "anim_preferences"
    }

    val preferences: Flow<AnimPreferences>

    suspend fun disableScheduleItemActionPreview()
    suspend fun disableTxInFolderItemActionPreview()
}