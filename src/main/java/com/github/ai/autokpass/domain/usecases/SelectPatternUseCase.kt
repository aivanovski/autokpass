package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.autotype.AutotypePatternFormatter
import com.github.ai.autokpass.presentation.selector.OptionSelector
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.Result

class SelectPatternUseCase(
    private val patternFormatter: AutotypePatternFormatter,
    private val optionSelector: OptionSelector
) {

    fun selectPattern(patterns: List<AutotypePattern>): Result<AutotypePattern?> {
        val options = patterns
            .mapIndexed { index, pattern ->
                "${index + 1} ${patternFormatter.format(pattern)}"
            }

        val selectionResult = optionSelector.select(options)
        if (selectionResult.isFailed()) {
            return selectionResult.asErrorOrThrow()
        }

        val selectionIdx = selectionResult.getDataOrThrow()
        return Result.Success(patterns[selectionIdx])
    }
}