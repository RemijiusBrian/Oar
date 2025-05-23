package dev.ridill.oar.core.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import dev.ridill.oar.core.ui.theme.spacing

// Spacers For Columns
@Composable
fun ColumnScope.SpacerExtraSmall() = VerticalSpacer(MaterialTheme.spacing.extraSmall)

@Composable
fun ColumnScope.SpacerSmall() = VerticalSpacer(MaterialTheme.spacing.small)

@Composable
fun ColumnScope.SpacerMedium() = VerticalSpacer(MaterialTheme.spacing.medium)

@Composable
fun ColumnScope.SpacerLarge() = VerticalSpacer(MaterialTheme.spacing.large)

@Composable
fun ColumnScope.SpacerExtraLarge() = VerticalSpacer(MaterialTheme.spacing.extraLarge)

@Composable
fun ColumnScope.Spacer(spacing: Dp) = VerticalSpacer(spacing)

@Composable
fun ColumnScope.Spacer(
    weight: Float
) = Spacer(modifier = Modifier.weight(weight))

@Composable
fun VerticalSpacer(
    spacing: Dp
) = Spacer(modifier = Modifier.height(spacing))

// Spacers For Rows
@Composable
fun RowScope.SpacerExtraSmall() = HorizontalSpacer(MaterialTheme.spacing.extraSmall)

@Composable
fun RowScope.SpacerSmall() = HorizontalSpacer(MaterialTheme.spacing.small)

@Composable
fun RowScope.SpacerMedium() = HorizontalSpacer(MaterialTheme.spacing.medium)

@Composable
fun RowScope.SpacerLarge() = HorizontalSpacer(MaterialTheme.spacing.large)

@Composable
fun RowScope.SpacerExtraLarge() = HorizontalSpacer(MaterialTheme.spacing.extraLarge)

@Composable
fun RowScope.Spacer(spacing: Dp) = HorizontalSpacer(spacing)

@Composable
fun RowScope.Spacer(
    weight: Float
) = Spacer(modifier = Modifier.weight(weight))

@Composable
fun HorizontalSpacer(
    spacing: Dp
) = Spacer(modifier = Modifier.width(spacing))
