package dev.ridill.oar.core.data.util

import dev.ridill.oar.R
import dev.ridill.oar.core.domain.model.DataError
import dev.ridill.oar.core.domain.model.Result
import dev.ridill.oar.core.domain.util.rethrowIfCoroutineCancellation
import dev.ridill.oar.core.domain.util.logE
import dev.ridill.oar.core.ui.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException

suspend inline fun <T, E : DataError> tryNetworkCall(
    crossinline call: suspend () -> Result<T, E>
): Result<T, DataError> = withContext(Dispatchers.IO) {
    try {
        call()
    } catch (e: IOException) {
        logE(e, "tryNetworkCall")
        Result.Error(
            DataError.Network.NO_INTERNET,
            UiText.StringResource(R.string.error_no_internet_connection)
        )
    } catch (e: SocketTimeoutException) {
        logE(e, "tryNetworkCall")
        Result.Error(
            DataError.Network.REQUEST_TIMEOUT,
            UiText.StringResource(R.string.error_request_timeout)
        )
    } catch (e: HttpException) {
        logE(e, "tryNetworkCall")
        when (e.code()) {
            in (500..599) -> Result.Error(
                DataError.Network.SERVER_ERROR,
                UiText.StringResource(R.string.error_server_error)
            )

            else -> Result.Error(
                DataError.Network.UNKNOWN,
                UiText.StringResource(R.string.error_unknown)
            )
        }
    } catch (t: Throwable) {
        t.rethrowIfCoroutineCancellation()
        logE(t, "tryNetworkCall")
        val message = t.message?.let {
            UiText.DynamicString(it)
        } ?: UiText.StringResource(R.string.error_unknown)

        Result.Error(DataError.Network.UNKNOWN, message)
    }
}

suspend inline fun <T> trySuspend(
    crossinline block: suspend () -> T
): T? = try {
    block()
} catch (t: Throwable) {
    t.rethrowIfCoroutineCancellation()
    logE(t, "trySuspend")
    null
}