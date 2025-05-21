package dev.ridill.oar.core.domain.service

import com.notkamui.keval.Keval
import dev.ridill.oar.core.domain.util.tryOrNull

class ExpEvalService {
    fun isExpression(value: String): Boolean = value.any { char ->
        char == '+'
                || char == '-'
                || char == '*'
                || char == '/'
                || char == '%'
                || char == '^'
    }

    fun evalOrNull(expression: String): Double? = tryOrNull {
        Keval.eval(expression)
    }
}