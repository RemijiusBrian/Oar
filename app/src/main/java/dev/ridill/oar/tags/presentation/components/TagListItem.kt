package dev.ridill.oar.tags.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.ExcludedIcon
import dev.ridill.oar.core.ui.components.ListItemLeadingContentContainer
import dev.ridill.oar.core.ui.components.icons.Tags
import dev.ridill.oar.core.ui.theme.IconSizeSmall
import dev.ridill.oar.core.ui.theme.elevation
import dev.ridill.oar.core.ui.theme.spacing
import dev.ridill.oar.core.ui.util.exclusionGraphicsLayer

@Composable
fun TagListItem(
    name: String,
    color: Color,
    excluded: Boolean,
    createdTimestamp: String,
    modifier: Modifier = Modifier,
    tonalElevation: Dp = MaterialTheme.elevation.level0
) {
    ListItem(
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                if (excluded) {
                    ExcludedIcon(
                        size = IconSizeSmall
                    )
                }
                Text(name)
            }
        },
        supportingContent = {
            Text(
                text = stringResource(
                    R.string.created_colon_timestamp_value,
                    createdTimestamp
                )
            )
        },
        leadingContent = {
            ListItemLeadingContentContainer(
                tonalElevation = MaterialTheme.elevation.level0
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tags,
                    contentDescription = null,
                    tint = color
                )
            }
        },
        modifier = modifier
            .exclusionGraphicsLayer(excluded),
        tonalElevation = tonalElevation
    )
}