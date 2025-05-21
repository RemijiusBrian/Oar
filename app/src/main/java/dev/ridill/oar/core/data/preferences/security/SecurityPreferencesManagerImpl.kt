package dev.ridill.oar.core.data.preferences.security

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.ridill.oar.core.domain.model.SecurityPreferences
import dev.ridill.oar.core.domain.util.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import java.io.IOException

class SecurityPreferencesManagerImpl(
    private val dataStore: DataStore<Preferences>
) : SecurityPreferencesManager {
    override val preferences: Flow<SecurityPreferences> = dataStore.data
        .catch { cause ->
            if (cause is IOException) {
                logE(cause) { "Preferences Exception" }
                emit(emptyPreferences())
            } else throw cause
        }
        .mapLatest { preferences ->
            val backupEncryptionHash = preferences[Keys.BACKUP_ENCRYPTION_HASH]
            val backupEncryptionHashSalt = preferences[Keys.BACKUP_ENCRYPTION_HASH_SALT]

            SecurityPreferences(
                backupEncryptionHash = backupEncryptionHash,
                backupEncryptionHashSalt = backupEncryptionHashSalt
            )
        }

    override suspend fun updateBackupEncryptionHash(hash: String?, salt: String?) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[Keys.BACKUP_ENCRYPTION_HASH] = hash.orEmpty()
                preferences[Keys.BACKUP_ENCRYPTION_HASH_SALT] = salt.orEmpty()
            }
        }
    }

    object Keys {
        val BACKUP_ENCRYPTION_HASH = stringPreferencesKey("BACKUP_ENCRYPTION_HASH")
        val BACKUP_ENCRYPTION_HASH_SALT = stringPreferencesKey("BACKUP_ENCRYPTION_HASH_SALT")
    }
}