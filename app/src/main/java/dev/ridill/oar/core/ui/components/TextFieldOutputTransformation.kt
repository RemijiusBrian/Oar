package dev.ridill.oar.core.ui.components

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.domain.util.logD
import java.util.Locale

class AmountOutputTransformation(
    private val locale: Locale = LocaleUtil.defaultLocale
) : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        val text = this.originalText
        logD("AmountOutputTransformation") { "text = $text" }
        val containsInvalidChars = text.any { !it.isDigit() }

        /*val formatted = if (containsInvalidChars) text
        else text.toString().toDoubleOrNull()?.let {
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
            }*/
    }
}

@Composable
fun rememberAmountOutputTransformation(): AmountOutputTransformation =
    remember { AmountOutputTransformation() }