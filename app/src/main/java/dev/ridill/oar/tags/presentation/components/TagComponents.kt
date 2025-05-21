package dev.ridill.oar.tags.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.ExcludedIcon
import dev.ridill.oar.core.ui.theme.contentColor
import dev.ridill.oar.core.ui.theme.spacing
import dev.ridill.oar.tags.domain.model.Tag

@Composable
fun RecentTagsSelectorFlowRow(
    recentTagsLazyPagingItems: LazyPagingItems<Tag>,
    selectedTagId: Long?,
    onTagClick: (Long) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
    ) {
        repeat(recentTagsLazyPagingItems.itemCount) { index ->
            recentTagsLazyPagingItems[index]?.let { tag ->
                TagChip(
                    name = tag.name,
                    color = tag.color,
                    excluded = tag.excluded,
                    selected = tag.id == selectedTagId,
                    onClick = { onTagClick(tag.id) }
                )
            }
        }

        ElevatedAssistChip(
            onClick = onViewAllClick,
            label = { Text(stringResource(R.string.view_all)) },
            border = null
        )
    }
}

@Composable
fun TagChip(
    name: String,
    color: Color,
    excluded: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) = FilterChip(
    selected = selected,
    onClick = onClick,
    label = {
        Text(
            text = name,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    },
    colors = tagChipColors(color = color),
    modifier = Modifier
        .widthIn(max = TagChipMaxWidth)
        .then(modifier),
//        .exclusionGraphicsLayer(excluded),
    enabled = enabled,
    leadingIcon = if (excluded) {
        { ExcludedIcon() }
    } else null
)

@Composable
fun ElevatedTagChip(
    name: String,
    color: Color,
    excluded: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) = ElevatedFilterChip(
    selected = true,
    onClick = {},
    label = {
        Text(
            text = name,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    },
    colors = tagChipColors(color = color),
    modifier = Modifier
        .widthIn(max = TagChipMaxWidth)
        .then(modifier),
//        .exclusionGraphicsLayer(excluded),
    enabled = enabled,
    leadingIcon = if (excluded) {
        { ExcludedIcon() }
    } else null
)

private val TagChipMaxWidth = 150.dp

@Composable
private fun tagChipColors(color: Color): SelectableChipColors = FilterChipDefaults.filterChipColors(
    selectedContainerColor = color,
    selectedLabelColor = color.contentColor(),
    selectedLeadingIconColor = color.contentColor(),
    iconColor = color
)