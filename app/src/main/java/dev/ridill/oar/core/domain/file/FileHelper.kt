package dev.ridill.oar.core.domain.file

import android.content.Context
import android.net.Uri
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.model.DataError
import dev.ridill.oar.core.domain.model.Result
import dev.ridill.oar.core.ui.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class FileHelper(
    context: Context
) {
    private val contentResolver = context.contentResolver

    suspend fun writeFileToUri(
        file: File,
        uri: Uri
    ): Result<Unit, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            file.inputStream().use { inputStream ->
                val bytes = readSafely(inputStream)
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    writeSafely(bytes, outputStream)
                }
            }
            Result.Success(Unit)
        } catch (_: Throwable) {
            Result.Error(
                DataError.Local.UNKNOWN,
                message = UiText.StringResource(R.string.error_failed_build_file, true)
            )
        }
    }

    suspend fun readSafely(
        inputStream: InputStream
    ): ByteArray = withContext(Dispatchers.IO) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            ensureActive()
            byteArrayOutputStream.write(buffer, 0, bytesRead)
        }
        return@withContext byteArrayOutputStream.toByteArray()
    }

    suspend fun writeSafely(
        byteArray: ByteArray,
        outputStream: OutputStream
    ) = withContext(Dispatchers.IO) {
        val byteArrayInputStream = ByteArrayInputStream(byteArray)
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytesRead: Int
        while (byteArrayInputStream.read(buffer).also { bytesRead = it } != -1) {
            ensureActive()
            outputStream.write(buffer, 0, bytesRead)
        }
    }

    object MimeType {
        const val OCTET_STREAM = "application/octet-stream"
    }
}