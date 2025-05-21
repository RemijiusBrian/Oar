package dev.ridill.oar.transactions.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.NewLine
import dev.ridill.oar.core.domain.util.One
import dev.ridill.oar.core.domain.util.WhiteSpace
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.core.domain.util.orZero
import dev.ridill.oar.core.ui.components.AmountWithTypeIndicator
import dev.ridill.oar.core.ui.components.ExcludedIndicatorSmall
import dev.ridill.oar.core.ui.components.LabelLargeText
import dev.ridill.oar.core.ui.components.ListItemLeadingContentContainer
import dev.ridill.oar.core.ui.theme.ContentAlpha
import dev.ridill.oar.core.ui.theme.OarTheme
import dev.ridill.oar.core.ui.theme.elevation
import dev.ridill.oar.core.ui.theme.spacing
import dev.ridill.oar.core.ui.util.exclusionGraphicsLayer
import dev.ridill.oar.transactions.domain.model.FolderIndicator
import dev.ridill.oar.transactions.domain.model.TagIndicator
import dev.ridill.oar.transactions.domain.model.TransactionType
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun TransactionListItem(
    note: String,
    amount: String,
    timeStamp: LocalDateTime,
    leadingContentLine1: String,
    leadingContentLine2: String,
    type: TransactionType,
    modifier: Modifier = Modifier,
    tag: TagIndicator? = null,
    folder: FolderIndicator? = null,
    excluded: Boolean = false,
    overlineContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation
) {
    val transactionListItemContentDescription = buildString {
        append(
            stringResource(
                when (type) {
                    TransactionType.CREDIT -> R.string.cd_transaction_list_item_credit
                    TransactionType.DEBIT -> R.string.cd_transaction_list_item_debit
                },
                amount,
                note,
                timeStamp.format(DateUtil.Formatters.localizedDateLong)
            )
        )

        tag?.let {
            append(String.WhiteSpace)
            append(stringResource(R.string.cd_transaction_list_item_tag_append, it.name))
        }

        folder?.let {
            append(String.WhiteSpace)
            append(stringResource(R.string.cd_transaction_list_item_folder_append, it.name))
        }
    }
    ListItem(
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                    if (note.isEmpty() && folder != null) {
                        FolderIndicator(
                            name = folder.name
                        )
                    } else {
                        NoteText(
                            note = note,
                            type = type
                        )
                    }
                }
            }
        },
        leadingContent = {
            DateAndTag(
                dateLine1 = leadingContentLine1,
                dateLine2 = leadingContentLine2,
                tag = tag,
                tonalElevation = tonalElevation + 2.dp
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                if (excluded) {
                    ExcludedIndicatorSmall()
                }
                AmountWithTypeIndicator(
                    value = amount,
                    type = type
                )
            }
        },
        supportingContent = {
            if (note.isNotEmpty()) {
                folder?.let {
                    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodySmall) {
                        FolderIndicator(
                            name = it.name
                        )
                    }
                }
            }
        },
        overlineContent = overlineContent,
        modifier = Modifier
            .semantics(mergeDescendants = true) {}
            .clearAndSetSemantics {
                contentDescription = transactionListItemContentDescription
            }
            .then(modifier)
            .exclusionGraphicsLayer(excluded),
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation
    )
}

@Composable
private fun NoteText(
    note: String,
    type: TransactionType,
    modifier: Modifier = Modifier
) {
    Text(
        text = note.ifEmpty { stringResource(type.labelRes) },
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
        color = LocalContentColor.current.copy(
            alpha = if (note.isEmpty()) ContentAlpha.SUB_CONTENT
            else Float.One
        ),
        style = LocalTextStyle.current.copy(
            fontStyle = if (note.isEmpty()) FontStyle.Italic
            else null,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun FolderIndicator(
    name: String,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalContentColor provides LocalContentColor.current
            .copy(alpha = ContentAlpha.SUB_CONTENT)
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_filled_folder),
                contentDescription = null,
                modifier = Modifier
                    .size(SmallIndicatorSize)
            )
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private val SmallIndicatorSize = 12.dp

@Composable
fun NewTransactionFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation()
) {
    FloatingActionButton(
        onClick = onClick,
        elevation = elevation,
        modifier = modifier
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_outline_money_add),
            contentDescription = stringResource(R.string.cd_new_transaction_fab)
        )
    }
}

@Composable
fun TypeIndicatorIcon(
    type: TransactionType,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = ImageVector.vectorResource(type.iconRes),
        contentDescription = stringResource(type.labelRes),
        tint = type.color,
        modifier = modifier
    )
}

@Composable
private fun DateAndTag(
    dateLine1: String,
    dateLine2: String,
    tag: TagIndicator?,
    modifier: Modifier = Modifier,
    tonalElevation: Dp = MaterialTheme.elevation.level1
) {
    Layout(
        modifier = modifier,
        content = {
            ListItemLeadingContentContainer(
                tonalElevation = tonalElevation
            ) {
                LabelLargeText(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                            append(dateLine1)
                        }
                        append(String.NewLine)
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                            append(dateLine2)
                        }
                    },
                    textAlign = TextAlign.Center
                )
            }
            tag?.let {
                TagColorIndicator(
                    color = it.color
                )
            }
        }
    ) { measurables, constraints ->
        val datePlaceable = measurables.first().measure(constraints)
        val tagIndicatorWidth = datePlaceable.width / TAG_INDICATOR_WIDTH_FRACTION
        val tagPlaceable = measurables.getOrNull(1)
            ?.measure(
                constraints = constraints.copy(
                    minWidth = tagIndicatorWidth,
                    maxWidth = tagIndicatorWidth,
                )
            )

        val totalHeight = datePlaceable.height + tagPlaceable?.height.orZero()

        layout(width = datePlaceable.width, height = totalHeight) {
            datePlaceable.placeRelative(0, tagPlaceable?.height.orZero() / 2)
            tagPlaceable?.placeRelative(
                (datePlaceable.width / 2) - (tagPlaceable.width.orZero() / 2),
                datePlaceable.height
            )
        }
    }
}

@Composable
private fun TagColorIndicator(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .height(TagIndicatorHeight)
            .clip(CircleShape)
            .background(color)
            .then(modifier)
    )
}

private const val TAG_INDICATOR_WIDTH_FRACTION = 4
private val TagIndicatorHeight = 4.dp

@PreviewLightDark
@Composable
private fun PreviewTransactionListItem() {
    OarTheme {
        TransactionListItem(
            note = "Note",
            amount = "Rs.1000",
            timeStamp = LocalDateTime.now(),
            leadingContentLine1 = LocalDate.now().dayOfMonth.toString(),
            leadingContentLine2 = LocalDate.now().month.toString(),
            type = TransactionType.CREDIT,
            modifier = Modifier,
            tag = TagIndicator(id = Long.Zero, name = "Test", color = Color.Yellow),
            folder = null,
            excluded = false
        )
    }
}