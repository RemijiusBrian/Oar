package dev.ridill.oar.core.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.domain.util.orZero
import dev.ridill.oar.core.ui.util.TextFormat
import java.util.Locale

class AmountVisualTransformation(
    private val locale: Locale = LocaleUtil.defaultLocale
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val containsInvalidChars = text.any { !it.isDigit() }
        val formatted = if (containsInvalidChars) text.text
        else text.text.toDoubleOrNull()?.let {
            TextFormat.number(value = it, locale = locale)
        }.orEmpty()
            .let { formatted ->
                val prefixZeroCount = text.takeWhile { it == '0' }.count()
                val padCount = formatted.length + prefixZeroCount
                    .takeIf { formatted != "0" }
                    .orZero()
                // Formatted amount padded with prefix 0s
                // to prevent app crash due to failed offset mapping
                // as prefix 0s are removed in formatted string

                formatted.padStart(padCount, '0')
            }

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = if (containsInvalidChars) OffsetMapping.Identity
            else object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val transformedOffsets = formatted.mapIndexedNotNull { index, c ->
                        index
                            .takeIf { c.isDigit() }
                            ?.plus(1)
                    }
                        .let { listOf(0) + it }
                    return transformedOffsets[offset]
                }

                override fun transformedToOriginal(offset: Int): Int =
                    formatted
                        .mapIndexedNotNull { index, c ->
                            index.takeIf { !c.isDigit() }
                        }
                        .count { it < offset }
                        .let { offset - it }
            }
        )
    }
}