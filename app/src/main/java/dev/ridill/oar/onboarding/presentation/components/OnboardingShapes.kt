package dev.ridill.oar.onboarding.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.tooling.preview.Preview
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import dev.ridill.oar.core.ui.theme.OarTheme
import dev.ridill.oar.onboarding.domain.model.OnboardingPage
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

private fun welcomeShape(size: Size): RoundedPolygon =
    RoundedPolygon(
        numVertices = 3,
        radius = size.minDimension / 2f,
        centerX = size.width / 2f,
        centerY = size.width / 2f
    )

private fun permissionsShape(size: Size): RoundedPolygon =
    RoundedPolygon(
        numVertices = 4,
        radius = size.minDimension / 2f,
        centerX = size.width / 2f,
        centerY = size.width / 2f
    )

private fun welcomeToPermissionsMorph(size: Size): Morph =
    Morph(welcomeShape(size), permissionsShape(size))

private fun backupShape(size: Size): RoundedPolygon =
    RoundedPolygon(
        numVertices = 5,
        radius = size.minDimension / 2f,
        centerX = size.width / 2f,
        centerY = size.width / 2f
    )

private fun permissionsToBackupMorph(size: Size): Morph =
    Morph(permissionsShape(size), backupShape(size))

private fun budgetShape(size: Size): RoundedPolygon =
    RoundedPolygon(
        numVertices = 6,
        radius = size.minDimension / 2f,
        centerX = size.width / 2f,
        centerY = size.width / 2f
    )

private fun backupToBudgetMorph(size: Size): Morph =
    Morph(backupShape(size), budgetShape(size))

@Composable
fun OnboardingShapes(
    currentPage: OnboardingPage,
    pageState: PagerState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .drawWithCache {
                val morph = when (currentPage) {
                    OnboardingPage.WELCOME -> welcomeToPermissionsMorph(size)
                    OnboardingPage.APP_PERMISSIONS -> welcomeToPermissionsMorph(size)
                    OnboardingPage.ACCOUNT_SIGN_IN_AND_DATA_RESTORE -> permissionsToBackupMorph(size)
                    OnboardingPage.SETUP_BUDGET_CYCLES -> backupToBudgetMorph(size)
                }

                onDrawBehind {
                    drawPath(
                        morph.toPath(pageState.currentPageOffsetFraction).asComposePath(),
                        color = Color.Black
                    )
                }
            }
    )
}

@Preview
@Composable
private fun PreviewOnboardingShapes() {
    OarTheme {
        var currentPage by remember { mutableStateOf(OnboardingPage.WELCOME) }
        val pagerState = rememberPagerState { OnboardingPage.entries.size }
        LaunchedEffect(Unit) {
            while (true) {
                currentPage = when (currentPage) {
                    OnboardingPage.WELCOME -> OnboardingPage.APP_PERMISSIONS
                    OnboardingPage.APP_PERMISSIONS -> OnboardingPage.ACCOUNT_SIGN_IN_AND_DATA_RESTORE
                    OnboardingPage.ACCOUNT_SIGN_IN_AND_DATA_RESTORE -> OnboardingPage.SETUP_BUDGET_CYCLES
                    OnboardingPage.SETUP_BUDGET_CYCLES -> OnboardingPage.WELCOME
                }
//                pagerState.animateScrollToPage(currentPage.ordinal, animationSpec = tween(2000))
                delay(2.seconds)
            }
        }
        Surface {
            OnboardingShapes(
                currentPage = currentPage,
                pageState = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}