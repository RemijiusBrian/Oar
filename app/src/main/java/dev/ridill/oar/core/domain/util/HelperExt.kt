package dev.ridill.oar.core.domain.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.nio.ByteBuffer
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

val Double.Companion.Zero: Double get() = 0.0
fun Double?.orZero(): Double = this ?: Double.Zero
inline fun Double.ifInfinite(value: () -> Double): Double = if (this.isInfinite()) value() else this
inline fun Double.ifNaN(value: () -> Double): Double = if (this.isNaN()) value() else this

val Float.Companion.Zero: Float get() = 0f
val Float.Companion.One: Float get() = 1f
fun Float?.orZero(): Float = this ?: Float.Zero
inline fun Float.ifNaN(value: () -> Float): Float = if (this.isNaN()) value() else this

val Int.Companion.Zero: Int get() = 0
val Int.Companion.One: Int get() = 1
fun Int?.orZero(): Int = this ?: Int.Zero
fun Int.toByteArray(): ByteArray = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(this).array()
fun ByteArray.toInt(): Int = ByteBuffer.wrap(this).int

val Long.Companion.Zero: Long get() = 0L
fun Long?.orZero(): Long = this ?: Long.Zero

val Dp.Companion.Zero: Dp get() = 0.dp

val String.Companion.Empty: String get() = ""
val String.Companion.WhiteSpace: String get() = " "
val String.Companion.NewLine: String get() = "\n"
fun String.toUUID(): UUID = UUID.nameUUIDFromBytes(this.toByteArray())

fun String.capitalizeFirstChar(): String =
    this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(LocaleUtil.defaultLocale) else it.toString() }

fun List<String>.joinToCapitalizedString(
    locale: Locale = LocaleUtil.defaultLocale
): String = buildString {
    this@joinToCapitalizedString.forEach { word ->
        if (word.length == 1) {
            append(word.uppercase(locale))
        } else {
            append(
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(LocaleUtil.defaultLocale)
                    else it.toString()
                }
            )
        }
        if (word != this@joinToCapitalizedString.lastOrNull()) append(String.WhiteSpace)
    }
}

fun Boolean?.orFalse(): Boolean = this ?: false
fun Boolean?.orTrue(): Boolean = this ?: true

fun <T> Set<T>.addOrRemove(element: T): Set<T> =
    if (element in this) this - element
    else this + element

fun <T> T.isAnyOf(vararg elements: T): Boolean = elements.any { it == this }

fun Throwable.rethrowIfCoroutineCancellation() {
    if (this is CancellationException) throw this
}

val WindowInsets.Companion.Zero: WindowInsets
    get() = WindowInsets(Dp.Zero)

fun TextFieldState.textAsFlow(): Flow<String> = snapshotFlow { this.text.toString() }
    .distinctUntilChanged()