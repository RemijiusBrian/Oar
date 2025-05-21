package dev.ridill.oar.tags.presentation.tagSelection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.ArrangementTopWithFooter
import dev.ridill.oar.core.ui.components.OarModalBottomSheet
import dev.ridill.oar.core.ui.components.SearchField
import dev.ridill.oar.core.ui.components.Spacer
import dev.ridill.oar.core.ui.components.TitleLargeText
import dev.ridill.oar.core.ui.theme.spacing
import dev.ridill.oar.tags.domain.model.Tag
import dev.ridill.oar.tags.presentation.components.TagChip

@Composable
fun TagSelectionSheet(
    queryState: TextFieldState,
    multiSelection: Boolean,
    tagsLazyPagingItems: LazyPagingItems<Tag>,
    onDismiss: () -> Unit,
    selectedIds: Set<Long>,
    onItemClick: (Long) -> Unit,
    onConfirm: () -> Unit,
    navigateToAddEditTag: () -> Unit,
    modifier: Modifier = Modifier
) {
    OarModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(MaterialTheme.spacing.medium),
            verticalArrangement = ArrangementTopWithFooter(MaterialTheme.spacing.small)
        ) {
            TitleLargeText(
                text = pluralStringResource(
                    id = R.plurals.select_tag,
                    count = if (multiSelection) 2 else 1
                )
            )
            SearchField(
                state = queryState,
                placeholder = stringResource(R.string.search_tags),
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextButton(
                onClick = navigateToAddEditTag,
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .size(ButtonDefaults.IconSize)
                )
                Spacer(spacing = ButtonDefaults.IconSpacing)
                Text(text = stringResource(R.string.create_new_tag))
            }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = TagsFlowRowMinHeight)
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(
                    space = MaterialTheme.spacing.small,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                repeat(tagsLazyPagingItems.itemCount) {
                    tagsLazyPagingItems[it]?.let { tag ->
                        TagChip(
                            name = tag.name,
                            color = Color(tag.colorCode),
                            excluded = tag.excluded,
                            selected = tag.id in selectedIds,
                            onClick = { onItemClick(tag.id) }
                        )
                    }
                }
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        }
    }
}

private val TagsFlowRowMinHeight = 200.dp