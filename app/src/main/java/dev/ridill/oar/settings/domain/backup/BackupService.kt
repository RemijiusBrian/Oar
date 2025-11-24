package dev.ridill.oar.settings.domain.backup

import android.content.Context
import dev.ridill.oar.R
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.domain.crypto.CryptoManager
import dev.ridill.oar.core.domain.crypto.HashSaltString
import dev.ridill.oar.core.domain.crypto.HashString
import dev.ridill.oar.core.domain.file.FileHelper
import dev.ridill.oar.core.domain.util.logI
import dev.ridill.oar.core.domain.util.toByteArray
import dev.ridill.oar.core.domain.util.toInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException

class BackupService(
    private val context: Context,
    private val database: OarDatabase,
    private val cryptoManager: CryptoManager,
    private val fileHelper: FileHelper,
) {

    companion object {
        fun dbBackupFileName(context: Context): String =
            "${context.getString(R.string.app_name)}_db.backup"
    }

    @Throws(
        RestoreFailedThrowable::class,
        BackupCachingFailedThrowable::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    suspend fun restoreBackupFromCache(
        passwordHash: HashString,
        passwordSalt: HashSaltString,
        timestamp: LocalDateTime
    ) = withContext(Dispatchers.IO) {
        val dbPath = database.openHelper.readableDatabase.path
        val dbFile = dbPath?.let { File(it) } ?: throw BackupCachingFailedThrowable()
        val dbWalFile = File(dbFile.path + SQLITE_WAL_FILE_SUFFIX)
        val dbShmFile = File(dbFile.path + SQLITE_SHM_FILE_SUFFIX)

        val cachePath = context.externalCacheDir ?: throw BackupCachingFailedThrowable()
        logI { "Create decrypted cache" }
        val decryptedDataCache = File(cachePath, DB_TEMP_CACHE_FILENAME)

        val encryptedDataCache = File(cachePath, buildRestoreCacheFileName(timestamp))
        if (!encryptedDataCache.exists()) throw RestoreFailedThrowable()

        encryptedDataCache.inputStream().buffered()
            .use encryptedDataCacheInputStream@{ inputStream ->
                val ivSizeBytes = ByteArray(Int.SIZE_BYTES)
                inputStream.read(ivSizeBytes)
                val ivSize = ivSizeBytes.toInt()

                logI { "Read iv data" }
                var ivBytesLeft = ivSize
                var ivBytes = ByteArray(0)
                while (ivBytesLeft > 0) {
                    ensureActive()
                    val data = ByteArray(minOf(DEFAULT_BUFFER_SIZE, ivBytesLeft))
                    val bytesRead = inputStream.read(data)
                    ivBytes += data.copyOfRange(0, bytesRead)
                    ivBytesLeft -= bytesRead
                }

                logI { "Read encrypted data" }
                val dataBytes = fileHelper.readSafely(inputStream)
                logI { "Decrypt data" }
                val decryptedBytes = cryptoManager.decrypt(
                    encryptedData = dataBytes,
                    iv = ivBytes,
                    password = passwordHash,
                    salt = passwordSalt
                )

                logI { "Write decrypted data to decrypted cache" }
                decryptedDataCache.outputStream().buffered().use decryptedDataCacheOutputStream@{
                    fileHelper.writeSafely(decryptedBytes, it)
                }
            }

        logI { "Write decrypted cache to DB files" }
        decryptedDataCache.inputStream().buffered().buffered()
            .use decryptedDataCacheInputStream@{ inputStream ->
                // Read DB Data
                dbFile.outputStream().buffered().use dbOutputStream@{
                    val dbSizeBytes = ByteArray(Int.SIZE_BYTES)
                    inputStream.read(dbSizeBytes)
                    val dbSize = dbSizeBytes.toInt()

                    var bytesLeft = dbSize
                    while (bytesLeft > 0) {
                        ensureActive()
                        val data = ByteArray(minOf(DEFAULT_BUFFER_SIZE, bytesLeft))
                        val bytesRead = inputStream.read(data)
                        bytesLeft -= bytesRead
                        it.write(data)
                    }
                }

                // Read WAL Data
                dbWalFile.outputStream().buffered().use walOutputStream@{
                    val walSizeBytes = ByteArray(Int.SIZE_BYTES)
                    val sizeBytesRead = inputStream.read(walSizeBytes)
                    if (sizeBytesRead == -1) return@walOutputStream
                    val walSize = walSizeBytes.toInt()

                    var bytesLeft = walSize
                    while (bytesLeft > 0) {
                        ensureActive()
                        val data = ByteArray(minOf(DEFAULT_BUFFER_SIZE, bytesLeft))
                        val bytesRead = inputStream.read(data)
                        bytesLeft -= bytesRead
                        it.write(data)
                    }
                }

                // Read SHM Data
                dbShmFile.outputStream().buffered().use shmOutputStream@{
                    val shmSizeBytes = ByteArray(Int.SIZE_BYTES)
                    val sizeBytesRead = inputStream.read(shmSizeBytes)
                    if (sizeBytesRead == -1) return@shmOutputStream
                    val shmSize = shmSizeBytes.toInt()

                    var bytesLeft = shmSize
                    while (bytesLeft > 0) {
                        ensureActive()
                        val data = ByteArray(minOf(DEFAULT_BUFFER_SIZE, bytesLeft))
                        val bytesRead = inputStream.read(data)
                        bytesLeft -= bytesRead
                        it.write(data)
                    }
                }
            }
        checkpointDb()
    }

    @Throws(
        BackupCachingFailedThrowable::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    suspend fun buildBackupFile(
        password: String,
        passwordSalt: HashSaltString,
    ): File = withContext(Dispatchers.IO) {
        val dbFile = context.getDatabasePath(OarDatabase.NAME)
        val dbWalFile = File(dbFile.path + SQLITE_WAL_FILE_SUFFIX)
        val dbShmFile = File(dbFile.path + SQLITE_SHM_FILE_SUFFIX)

        val cachePath = context.externalCacheDir ?: throw BackupCachingFailedThrowable()
        logI { "Create temp backup cache file" }
        val dbCache = File(cachePath, DB_TEMP_CACHE_FILENAME)
        if (dbCache.exists()) dbCache.delete()

        logI { "Checkpoint DB" }
        checkpointDb()
        logI { "Read DB data into temp backup cache file" }
        dbCache.outputStream().buffered().use tempCacheOutputStream@{ outputStream ->
            // Write DB Data
            dbFile.inputStream().buffered().use dbInputStream@{
                val dbData = fileHelper.readSafely(it)
                outputStream.write(dbData.size.toByteArray())
                fileHelper.writeSafely(dbData, outputStream)
            }

            // Write WAL Data
            if (dbWalFile.exists()) dbWalFile.inputStream().buffered().use walInputStream@{
                val walData = fileHelper.readSafely(it)
                outputStream.write(walData.size.toByteArray())
                fileHelper.writeSafely(walData, outputStream)
            }

            // Write SHM Data
            if (dbShmFile.exists()) dbShmFile.inputStream().buffered().use shmInputStream@{
                val shmData = fileHelper.readSafely(it)
                outputStream.write(shmData.size.toByteArray())
                fileHelper.writeSafely(shmData, outputStream)
            }
        }

        logI { "Create DB backup file" }
        val encryptedBackupFile = File(cachePath, dbBackupFileName(context))
        if (encryptedBackupFile.exists()) encryptedBackupFile.delete()
        dbCache.inputStream().buffered().use dbCacheInputStream@{
            val rawBytes = fileHelper.readSafely(it)
            logI { "Encrypt temp backup cache data" }
            val encryptionResult = cryptoManager.encrypt(
                rawData = rawBytes,
                password = password,
                salt = passwordSalt
            )
            encryptedBackupFile.outputStream().buffered()
                .use backupFileOutputStream@{ outputStream ->
                    logI { "Write encrypted temp backup cache data to backup file" }
                    fileHelper.writeSafely(encryptionResult.iv.size.toByteArray(), outputStream)
                    fileHelper.writeSafely(encryptionResult.iv, outputStream)
                    fileHelper.writeSafely(encryptionResult.data, outputStream)
                }
        }

        encryptedBackupFile
    }

    suspend fun clearCache() = withContext(Dispatchers.IO) {
        val cacheDir = context.externalCacheDir
        cacheDir?.delete()
        logI { "Cleared local cacheDir" }
    }

    private fun checkpointDb() {
        val writableDb = database.openHelper.writableDatabase
        writableDb.query("PRAGMA wal_checkpoint(FULL);")
        writableDb.query("PRAGMA wal_checkpoint(TRUNCATE);")
    }

    fun doesRestoreCacheExist(timestamp: LocalDateTime): Boolean {
        val cachePath = context.externalCacheDir ?: throw RestoreFailedThrowable()
        val restoreCacheFile = File(cachePath, buildRestoreCacheFileName(timestamp))
        return restoreCacheFile.exists()
    }

    @Throws(RestoreFailedThrowable::class)
    suspend fun cacheDownloadedRestoreData(
        dataStream: InputStream,
        dateTime: LocalDateTime,
        refreshCache: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val cachePath = context.externalCacheDir ?: throw RestoreFailedThrowable()
        logI { "Create Restore Data Cache" }
        val restoreDataCache = File(cachePath, buildRestoreCacheFileName(dateTime))
        if (restoreDataCache.exists() && !refreshCache) return@withContext
        dataStream.use downloadedInputStream@{ inputStream ->
            restoreDataCache.outputStream().buffered()
                .use restoreCacheOutputStream@{ outputStream ->
                    val data = fileHelper.readSafely(inputStream)
                    fileHelper.writeSafely(data, outputStream)
                }
        }
    }

    private fun buildRestoreCacheFileName(timestamp: LocalDateTime): String =
        "$timestamp-$RESTORE_CACHE_FILE"
}

private const val DB_TEMP_CACHE_FILENAME = "DBBackupCache.backup"
private const val SQLITE_WAL_FILE_SUFFIX = "-wal"
private const val SQLITE_SHM_FILE_SUFFIX = "-shm"
private const val RESTORE_CACHE_FILE = "RestoreCache.backup"

class BackupCachingFailedThrowable : Throwable("Failed to create backup cache")
class RestoreFailedThrowable : Throwable("Failed to restore data")