package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.autotype.AutotypePatternFormatter
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.selector.OptionSelector
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.lang.Exception

class SelectPatternUseCaseTest {

    private val patternFormatter = AutotypePatternFormatter()
    private val optionSelector = mockk<OptionSelector>()

    private val patterns = AutotypePattern.ALL
    private val formattedPatterns = patterns
        .mapIndexed { index, pattern ->
            "${index + 1} ${patternFormatter.format(pattern)}"
        }

    @Test
    fun `selectPattern should return selected pattern`() {
        // arrange
        every { optionSelector.select(formattedPatterns) }.returns(Result.Success(SELECTED_PATTERN_IDX))

        // act
        val result = createUseCase().selectPattern(patterns)

        // assert
        verify { optionSelector.select(formattedPatterns) }

        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(patterns[SELECTED_PATTERN_IDX])
    }

    @Test
    fun `selectPattern should return error`() {
        // arrange
        val exception = Exception()
        every { optionSelector.select(formattedPatterns) }.returns(Result.Error(exception))

        // act
        val result = createUseCase().selectPattern(patterns)

        // assert
        verify { optionSelector.select(formattedPatterns) }

        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isSameInstanceAs(exception)
    }

    private fun createUseCase(): SelectPatternUseCase {
        return SelectPatternUseCase(
            patternFormatter = patternFormatter,
            optionSelector = optionSelector
        )
    }

    companion object {
        private const val SELECTED_PATTERN_IDX = 2
    }
}