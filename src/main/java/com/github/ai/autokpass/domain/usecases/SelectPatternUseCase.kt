package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.autotype.AutotypePatternFormatter
import com.github.ai.autokpass.presentation.selector.OptionSelector
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.Result

class SelectPatternUseCase(
    private val patternFormatter: AutotypePatternFormatter,
    private val optionSelector: OptionSelector
) {

    fun selectPattern(): Result<AutotypePattern?> {
        val options = PATTERNS
            .mapIndexed { index, pattern ->
                (index + 1).toString() + " " + patternFormatter.format(pattern)
            }

        val selectionResult = optionSelector.select(options)
        if (selectionResult.isFailed()) {
            return selectionResult.getErrorOrThrow()
        }

        val selectionIdx = selectionResult.getDataOrThrow()
        return Result.Success(PATTERNS[selectionIdx])
    }

    companion object {
        private val PATTERNS = listOf(
            AutotypePattern.DEFAULT_PATTERN,
            AutotypePattern.USERNAME_WITH_ENTER,
            AutotypePattern.PASSWORD_WITH_ENTER,
            AutotypePattern.USERNAME,
            AutotypePattern.PASSWORD,
        )
    }
}