package dev.ridill.oar.core.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import dev.ridill.oar.core.ui.theme.spacing
import dev.ridill.oar.settings.presentation.components.PreferenceIconSize

@Composable
fun OptionListItem(
    @DrawableRes iconRes: Int,
    label: String,
    onClick: () -> Unit,
    onClickLabel: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(
                onClick = onClick,
                onClickLabel = onClickLabel
            )
            .minimumInteractiveComponentSize()
            .padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.small
            )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(PreferenceIconSize)
        )
        SpacerMedium()
        BodyMediumText(label)
    }
}