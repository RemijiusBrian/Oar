package dev.ridill.oar.core.ui.navigation.destinations

import android.os.Parcelable
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import dev.ridill.oar.R
import dev.ridill.oar.core.ui.components.navigateUpWithResult
import dev.ridill.oar.transactions.domain.model.AmountTransformation
import dev.ridill.oar.transactions.presentation.amountTransformation.AmountTransformationSheet
import dev.ridill.oar.transactions.presentation.amountTransformation.AmountTransformationViewModel
import kotlinx.parcelize.Parcelize

data object AmountTransformationSheetSpec : BottomSheetSpec {

    const val TRANSFORMATION_RESULT = "TRANSFORMATION_RESULT"

    override val route: String
        get() = "amount_transformation_sheet"

    override val labelRes: Int
        get() = R.string.destination_amount_transformation_selection

    @Composable
    override fun Content(
        windowSizeClass: WindowSizeClass,
        navController: NavHostController,
        navBackStackEntry: NavBackStackEntry
    ) {
        val viewModel: AmountTransformationViewModel = hiltViewModel(navBackStackEntry)
        val selectedTransformation by viewModel.selectedTransformation.collectAsStateWithLifecycle()

        AmountTransformationSheet(
            onDismiss = navController::navigateUp,
            selectedTransformation = selectedTransformation,
            onTransformationSelect = viewModel::onTransformationSelect,
            factorInput = viewModel.factorInputState,
            onTransformClick = {
                navController.navigateUpWithResult(
                    TRANSFORMATION_RESULT,
                    TransformationResult(
                        transformation = selectedTransformation,
                        factor = viewModel.factorInputState.text.toString()
                    )
                )
            }
        )
    }
}

@Parcelize
data class TransformationResult(
    val transformation: AmountTransformation,
    val factor: String
) : Parcelable