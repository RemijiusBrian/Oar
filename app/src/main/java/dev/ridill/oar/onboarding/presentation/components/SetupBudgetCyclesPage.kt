package dev.ridill.oar.onboarding.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ridill.oar.R
import dev.ridill.oar.budgetCycles.domain.model.CycleStartDay
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.core.domain.util.logD
import dev.ridill.oar.core.ui.components.DisplayMediumText
import dev.ridill.oar.core.ui.components.OarTextField
import dev.ridill.oar.core.ui.components.SpacerSmall
import dev.ridill.oar.core.ui.components.rememberAmountOutputTransformation
import dev.ridill.oar.core.ui.theme.BorderWidthStandard
import dev.ridill.oar.core.ui.theme.ContentAlpha
import dev.ridill.oar.core.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest
import java.time.DayOfWeek
import java.util.Currency

@Composable
fun SetupBudgetCyclesPage(
    cycleStartDay: CycleStartDay,
    showDayOfWeekSelection: Boolean,
    selectedDayOfWeeks: Set<DayOfWeek>,
    inputState: TextFieldState,
    selectedCurrency: Currency,
    onCurrencyClick: () -> Unit,
    onStartBudgetingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isInputNotEmpty by remember {
        derivedStateOf { inputState.text.isNotEmpty() }
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.large)
            .verticalScroll(rememberScrollState())
    ) {
        DisplayMediumText(
            text = stringResource(R.string.onboarding_page_setup_budget_cycle_title),
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.medium)
        )

        NumberPicker(
            modifier = Modifier
                .fillMaxWidth()
        )

//        SpacerExtraLarge()
        BudgetInput(
            state = inputState,
            selectedCurrency = selectedCurrency,
            onCurrencyClick = onCurrencyClick
        )
        SpacerSmall()
        AnimatedVisibility(
            visible = isInputNotEmpty,
            modifier = Modifier
                .align(Alignment.End)
        ) {
            Button(
                onClick = {
                    keyboardController?.hide()
                    onStartBudgetingClick()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(stringResource(R.string.start_budgeting))
            }
        }
    }
}

@Composable
private fun NumberPicker(
    modifier: Modifier = Modifier
) {
}

@Composable
private fun CurrentPickedDate(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .heightIn()
    )
}

private val DateSliderItemMinHeight = 24.dp

@Composable
private fun BudgetInput(
    state: TextFieldState,
    selectedCurrency: Currency,
    onCurrencyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = LocalContentColor.current
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.onboarding_page_set_budget_message),
            style = MaterialTheme.typography.labelMedium
        )
        OarTextField(
            state = state,
            modifier = Modifier
                .fillMaxWidth(),
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.colors(
                focusedTextColor = contentColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = contentColor,
                unfocusedIndicatorColor = contentColor,
                unfocusedPlaceholderColor = contentColor,
                focusedPlaceholderColor = contentColor,
                unfocusedSupportingTextColor = contentColor,
                focusedSupportingTextColor = contentColor
            ),
            shape = MaterialTheme.shapes.medium,
            placeholder = { Text(stringResource(R.string.enter_budget)) },
            supportingText = { Text(stringResource(R.string.you_can_change_budget_later_in_settings)) },
            outputTransformation = rememberAmountOutputTransformation(),
            prefix = {
                OutlinedIconButton(
                    onClick = onCurrencyClick,
                    border = BorderStroke(BorderWidthStandard, contentColor)
                ) {
                    Text(
                        text = selectedCurrency.symbol,
                        color = contentColor
                    )
                }
            }
        )
    }
}