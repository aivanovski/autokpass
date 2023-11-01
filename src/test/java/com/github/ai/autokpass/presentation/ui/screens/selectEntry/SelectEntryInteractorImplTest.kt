package com.github.ai.autokpass.presentation.ui.screens.selectEntry

import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.ENTRIES
import com.github.ai.autokpass.TestData.EXCEPTION
import com.github.ai.autokpass.domain.formatter.DefaultEntryFormatter
import com.github.ai.autokpass.domain.fuzzySearch.Fzf4jFuzzyMatcher
import com.github.ai.autokpass.domain.usecases.GetVisibleEntriesUseCase
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.model.SearchItem
import com.github.ai.autokpass.utils.TestDispatchers
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SelectEntryInteractorImplTest {

    private val getEntriesUseCase = mockk<GetVisibleEntriesUseCase>()
    private val formatter = DefaultEntryFormatter()

    @Nested
    @DisplayName("loadAllEntries")
    inner class LoadAllEntries {

        @Test
        fun `should return entries and titles`() = runTest(UnconfinedTestDispatcher()) {
            // arrange
            val interactor = newInteractor()
            val expectedEntries = ENTRIES.filter { entry -> entry.isAutotypeEnabled }

            every {
                getEntriesUseCase.getEntries(
                    newKey(),
                    DB_PATH
                )
            }.returns(Result.Success(ENTRIES))

            // act
            val result = interactor.loadAllEntries(newKey(), DB_PATH)

            // assert
            result shouldBe Result.Success(
                Pair(
                    expectedEntries,
                    expectedEntries.formatAsText()
                )
            )
        }

        @Test
        fun `should return error`() = runTest(UnconfinedTestDispatcher()) {
            // arrange
            val interactor = newInteractor()
            val error = Result.Error(EXCEPTION)

            every { getEntriesUseCase.getEntries(newKey(), DB_PATH) }.returns(error)

            // act
            val result = interactor.loadAllEntries(newKey(), DB_PATH)

            // assert
            result shouldBe error
        }
    }

    @Nested
    @DisplayName("filterEntries")
    inner class FilterEntries {

        @Test
        fun `should return all entries if query is empty`() = runTest(UnconfinedTestDispatcher()) {
            // arrange
            val interactor = newInteractor()

            // act
            val result = interactor.filterEntries(ENTRIES, ENTRIES.formatAsText(), "")

            // assert
            result shouldBe Result.Success(ENTRIES.toSearchItems())
        }

        @Test
        fun `should return filtered entries`() = runTest(UnconfinedTestDispatcher()) {
            // arrange
            val interactor = newInteractor()
            val matchedEntry = ENTRIES.first()
            val expected = listOf(matchedEntry).toSearchItems(
                isHighlight = true,
                query = matchedEntry.title
            )

            // act
            val result = interactor.filterEntries(
                ENTRIES,
                ENTRIES.formatAsText(),
                matchedEntry.title
            )

            // assert
            result shouldBe Result.Success(expected)
        }
    }

    private fun List<KeepassEntry>.toSearchItems(
        isHighlight: Boolean = false,
        query: String = ""
    ): List<SearchItem> {
        return this.map { entry ->
            val text = formatter.format(entry)
            val highlights = if (isHighlight) {
                val startIdx = text.indexOf(query)
                val endIdx = startIdx + query.length - 1
                (startIdx..endIdx).toList()
            } else {
                emptyList()
            }

            SearchItem(
                entry = entry,
                text = formatter.format(entry),
                highlights = highlights
            )
        }
    }

    private fun List<KeepassEntry>.formatAsText(): List<String> {
        return this.map { entry -> formatter.format(entry) }
    }

    private fun newInteractor(): SelectEntryInteractor =
        SelectEntryInteractorImpl(
            getEntriesUseCase = getEntriesUseCase,
            dispatchers = TestDispatchers(),
            fuzzyMatcher = Fzf4jFuzzyMatcher(),
            formatter = formatter
        )

    private fun newKey(): KeepassKey =
        KeepassKey.PasswordKey(DB_PASSWORD)
}