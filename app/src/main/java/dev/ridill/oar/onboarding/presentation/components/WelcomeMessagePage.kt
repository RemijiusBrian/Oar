package dev.ridill.oar.onboarding.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.LottieAnim
import dev.ridill.oar.core.ui.components.DisplayMediumText
import dev.ridill.oar.core.ui.theme.spacing

@Composable
fun WelcomeMessagePage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        DisplayMediumText(
            text = stringResource(
                R.string.onboarding_page_welcome_title,
                stringResource(R.string.app_name)
            ),
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.medium)
        )

        LottieAnim(
            resId = R.raw.lottie_wallet,
            modifier = Modifier
                .fillMaxWidth()
                .height(WelcomeAnimHeight)
        )
    }
}

private val WelcomeAnimHeight = 200.dp