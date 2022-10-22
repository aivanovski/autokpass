package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.TestData.DB_WITH_PASSWORD
import com.github.ai.autokpass.domain.formatter.DefaultEntryFormatter
import com.github.ai.autokpass.getFilePath
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.selector.OptionSelector
import com.github.ai.autokpass.toKeepassKey
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Test
import java.lang.Exception

class SelectEntryUseCaseTest {

    private val getEntriesUseCase = mockk<GetVisibleEntriesUseCase>()
    private val entryFormatter = DefaultEntryFormatter()
    private val optionSelector = mockk<OptionSelector>()

    private val db = DB_WITH_PASSWORD
    private val dbFilePath = db.getFilePath()
    private val key = db.key.toKeepassKey()
    private val entries = db.entries
    private val formattedEntries = entries.map { entryFormatter.format(it) }

    @Test
    fun `selectEntry should return selected entry`() {
        // arrange
        val selectedEntry = entries[SELECTED_ENTRY_INDEX]

        every { getEntriesUseCase.getEntries(key, dbFilePath) }.returns(Result.Success(entries))
        every { optionSelector.select(formattedEntries) }.returns(Result.Success(SELECTED_ENTRY_INDEX))

        // act
        val result = createUseCase().selectEntry(key, dbFilePath)

        // assert
        verifySequence {
            getEntriesUseCase.getEntries(key, dbFilePath)
            optionSelector.select(formattedEntries)
        }

        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(selectedEntry)
    }

    @Test
    fun `selectEntry should return error if GetAllEntriesUseCase return error`() {
        // arrange
        val exception = Exception()
        every { getEntriesUseCase.getEntries(key, dbFilePath) }.returns(Result.Error(exception))

        // act
        val result = createUseCase().selectEntry(key, dbFilePath)

        // assert
        verifySequence {
            getEntriesUseCase.getEntries(key, dbFilePath)
        }

        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isSameInstanceAs(exception)
    }

    @Test
    fun `selectEntry should return error if `() {
        // arrange
        val exception = Exception()
        every { getEntriesUseCase.getEntries(key, dbFilePath) }.returns(Result.Success(entries))
        every { optionSelector.select(formattedEntries) }.returns(Result.Error(exception))

        // act
        val result = createUseCase().selectEntry(key, dbFilePath)

        // assert
        verifySequence {
            getEntriesUseCase.getEntries(key, dbFilePath)
            optionSelector.select(formattedEntries)
        }

        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isSameInstanceAs(exception)
    }

    private fun createUseCase(): SelectEntryUseCase {
        return SelectEntryUseCase(
            getEntriesUseCase = getEntriesUseCase,
            entryFormatter = entryFormatter,
            optionSelector = optionSelector
        )
    }

    companion object {
        private const val SELECTED_ENTRY_INDEX = 4
    }
}