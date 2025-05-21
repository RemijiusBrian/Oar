package dev.ridill.oar.settings.presentation.securitySettings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.ManualLottieAnim
import dev.ridill.oar.core.ui.theme.spacing
import dev.ridill.oar.core.ui.util.UiText

@Composable
fun AppLockedOverlay(
    unlockAnimProgress: State<Float>,
    onUnlockClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: UiText? = null
) {
    BackHandler(
        enabled = true,
        onBack = onBack
    )

    val showError by remember(errorMessage) {
        derivedStateOf { errorMessage != null }
    }

    Surface(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            onClick = {
                                if (unlockAnimProgress.value > 0f) return@clickable
                                onUnlockClick()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    ManualLottieAnim(
                        resId = R.raw.lottie_fingerprint_success,
                        progress = { unlockAnimProgress.value },
                        modifier = Modifier
                            .size(FingerprintIconSize)
                    )
                }

                Text(
                    text = stringResource(R.string.tap_to_unlock),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                AnimatedVisibility(visible = showError) {
                    Text(
                        text = errorMessage?.asString().orEmpty(),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private val FingerprintIconSize = 120.dp