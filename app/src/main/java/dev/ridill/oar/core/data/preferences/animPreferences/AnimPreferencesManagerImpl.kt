package dev.ridill.oar.core.data.preferences.animPreferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import dev.ridill.oar.core.domain.model.AnimPreferences
import dev.ridill.oar.core.domain.util.logE
import dev.ridill.oar.core.domain.util.orTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import java.io.IOException

class AnimPreferencesManagerImpl(
    private val dataStore: DataStore<Preferences>
) : AnimPreferencesManager {

    override val preferences: Flow<AnimPreferences> = dataStore.data
        .catch { cause ->
            if (cause is IOException) {
                logE(cause) { "Preferences Exception" }
                emit(emptyPreferences())
            } else throw cause
        }
        .mapLatest { preferences ->
            val showScheduleItemActionPreview =
                preferences[Keys.SHOW_SCHEDULE_ITEM_ACTION_PREVIEW].orTrue()
            val showTxInFolderItemActionPreview =
                preferences[Keys.SHOW_TX_IN_FOLDER_ITEM_ACTION_PREVIEW].orTrue()

            AnimPreferences(
                showScheduleItemActionPreview = showScheduleItemActionPreview,
                showTxInFolderItemActionPreview = showTxInFolderItemActionPreview
            )
        }

    override suspend fun disableScheduleItemActionPreview() {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[Keys.SHOW_SCHEDULE_ITEM_ACTION_PREVIEW] = false
            }
        }
    }

    override suspend fun disableTxInFolderItemActionPreview() {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[Keys.SHOW_TX_IN_FOLDER_ITEM_ACTION_PREVIEW] = false
            }
        }
    }

    private object Keys {
        val SHOW_SCHEDULE_ITEM_ACTION_PREVIEW =
            booleanPreferencesKey("SHOW_SCHEDULE_ITEM_ACTION_PREVIEW")
        val SHOW_TX_IN_FOLDER_ITEM_ACTION_PREVIEW =
            booleanPreferencesKey("SHOW_TX_IN_FOLDER_ITEM_ACTION_PREVIEW")
    }
}