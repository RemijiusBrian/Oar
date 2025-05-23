package dev.ridill.oar.settings.presentation.currencyUpdate

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.LabelledRadioButton
import dev.ridill.oar.core.ui.components.ListSearchSheet
import dev.ridill.oar.core.ui.navigation.destinations.CurrencySelectionSheetSpec
import java.util.Currency

@Composable
fun CurrencySelectionSheet(
    searchQueryState: TextFieldState,
    selectedCode: String?,
    currenciesPagingData: LazyPagingItems<Currency>,
    onDismiss: () -> Unit,
    onConfirm: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    ListSearchSheet(
        inputState = searchQueryState,
        onDismiss = onDismiss,
        title = stringResource(CurrencySelectionSheetSpec.labelRes),
        placeholder = stringResource(R.string.search_currency),
        modifier = modifier
    ) {
        items(
            count = currenciesPagingData.itemCount,
            key = currenciesPagingData.itemKey { it.currencyCode },
            contentType = currenciesPagingData.itemContentType { "CurrencySelector" }
        ) { index ->
            currenciesPagingData[index]?.let { currency ->
                LabelledRadioButton(
                    label = "${currency.displayName} (${currency.currencyCode})",
                    selected = currency.currencyCode == selectedCode,
                    onClick = { onConfirm(currency) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }
        }
    }
}