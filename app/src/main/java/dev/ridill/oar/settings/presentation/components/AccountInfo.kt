package dev.ridill.oar.settings.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.ridill.oar.account.domain.model.UserAccount
import dev.ridill.oar.core.ui.components.OarImage
import dev.ridill.oar.core.ui.components.TitleMediumText
import dev.ridill.oar.core.ui.theme.spacing

@Composable
fun LoggedInAccountInfo(
    account: UserAccount,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        OarImage(
            url = account.photoUrl,
            contentDescription = account.displayName,
            size = 24.dp
        )

        TitleMediumText(
            text = account.displayName
        )
    }
}