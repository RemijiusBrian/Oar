package dev.ridill.oar.budgetCycles.presentation.cycleSelection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import dev.ridill.oar.R
import dev.ridill.oar.budgetCycles.domain.model.CycleSelector
import dev.ridill.oar.core.ui.components.ListSearchSheet
import dev.ridill.oar.core.ui.navigation.destinations.CycleSelectionSheetSpec
import dev.ridill.oar.core.ui.theme.spacing

@Composable
fun CycleSelectionSheet(
    queryState: TextFieldState,
    cyclesLazyPagingItems: LazyPagingItems<CycleSelector>,
    selectedId: Long?,
    onCycleSelect: (Long) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ListSearchSheet(
        inputState = queryState,
        onDismiss = onDismiss,
        title = stringResource(CycleSelectionSheetSpec.labelRes),
        placeholder = stringResource(R.string.search_cycle),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        additionalEndContent = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.medium)
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        }
    ) {
        items(
            count = cyclesLazyPagingItems.itemCount,
            key = cyclesLazyPagingItems.itemKey { it.id },
            contentType = cyclesLazyPagingItems.itemContentType { CycleSelector::class }
        ) { index ->
            cyclesLazyPagingItems[index]?.let { cycle ->
                CycleSelectionItem(
                    description = cycle.description,
                    selected = cycle.id == selectedId,
                    onClick = { onCycleSelect(cycle.id) },
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .animateItem()
                )
            }
        }
    }
}

@Composable
private fun CycleSelectionItem(
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(description) },
        modifier = modifier
            .clickable(
                onClick = onClick,
                onClickLabel = stringResource(R.string.cd_tap_to_select_cycle, description)
            ),
        colors = ListItemDefaults.colors(
            containerColor = if (selected) MaterialTheme.colorScheme.surfaceContainerHigh
            else BottomSheetDefaults.ContainerColor
        )
    )
}