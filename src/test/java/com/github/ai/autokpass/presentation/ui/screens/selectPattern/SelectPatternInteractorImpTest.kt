package com.github.ai.autokpass.presentation.ui.screens.selectPattern

import com.github.ai.autokpass.TestData.ENTRY1
import com.github.ai.autokpass.domain.autotype.AutotypePatternFactory
import com.github.ai.autokpass.domain.autotype.AutotypePatternFormatter
import com.github.ai.autokpass.domain.fuzzySearch.Fzf4jFuzzyMatcher
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.model.SearchItem
import com.github.ai.autokpass.utils.TestDispatchers
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SelectPatternInteractorImpTest {

    private val factory = mockk<AutotypePatternFactory>()

    @Test
    fun `loadData should load data from factory`() = runTest(UnconfinedTestDispatcher()) {
        // arrange
        every { factory.createPatternsForEntry(ENTRY1) }.returns(PATTERNS)

        // act
        val result = newInteractor().loadData(ENTRY1)

        // assert
        verify { factory.createPatternsForEntry(ENTRY1) }
        result shouldBe Pair(
            PATTERNS,
            PATTERNS.formatToText()
        )
    }

    @Test
    fun `filter should return filtered entries`() = runTest(UnconfinedTestDispatcher()) {
        // arrange
        val query = "{USERNAME}"
        val expected = PATTERNS.zip(PATTERNS.formatToText())
            .filter { (_, title) ->
                title.contains(query)
            }
            .map { (pattern, title) ->
                val startIdx = title.indexOf(query)
                val endIdx = startIdx + query.length - 1

                SearchItem(
                    pattern = pattern,
                    text = title,
                    highlights = (startIdx..endIdx).toList()
                )
            }

        // act
        val result = newInteractor().filter(
            query = query,
            patterns = PATTERNS,
            titles = PATTERNS.formatToText()
        )

        // assert
        result shouldBe expected
    }

    private fun List<AutotypePattern>.formatToText(): List<String> {
        val formatter = AutotypePatternFormatter()

        return this.mapIndexed { index, pattern ->
            "${index + 1} ${formatter.format(pattern)}"
        }
    }

    private fun newInteractor(
        factory: AutotypePatternFactory = this.factory
    ): SelectPatternInteractor {
        return SelectPatternInteractorImpl(
            dispatchers = TestDispatchers(),
            factory = factory,
            formatter = AutotypePatternFormatter(),
            fuzzyMatcher = Fzf4jFuzzyMatcher()
        )
    }

    companion object {
        private val PATTERNS = AutotypePattern.ALL
    }
}