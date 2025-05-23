package dev.ridill.oar.transactions.presentation.amountTransformation

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.transactions.domain.model.AmountTransformation
import javax.inject.Inject

@HiltViewModel
class AmountTransformationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val selectedTransformation = savedStateHandle
        .getStateFlow(SELECTED_TRANSFORMATION, AmountTransformation.DIVIDE_BY)

    val factorInputState = TextFieldState()

    fun onTransformationSelect(transformation: AmountTransformation) {
        savedStateHandle[SELECTED_TRANSFORMATION] = transformation
    }
}

private const val SELECTED_TRANSFORMATION = "SELECTED_TRANSFORMATION"