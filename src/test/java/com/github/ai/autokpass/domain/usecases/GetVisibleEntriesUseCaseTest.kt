package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.ENTRIES
import com.github.ai.autokpass.TestData.ENTRY1
import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class GetVisibleEntriesUseCaseTest {

    @Test
    fun `getAllEntries should return entries with enabled autotype`() {
        // arrange
        val key = KeepassKey.PasswordKey(DB_PASSWORD)
        val readDbUseCase = mockk<ReadDatabaseUseCase>()
        val db = mockk<KeepassDatabase>()

        every { readDbUseCase.readDatabase(key, DB_PATH) }.returns(Result.Success(db))
        every { db.getAllEntries() }.returns(ENTRIES)

        // act
        val result = GetVisibleEntriesUseCase(readDbUseCase)
            .getEntries(key, DB_PATH)

        // assert
        verify { readDbUseCase.readDatabase(key, DB_PATH) }
        verify { db.getAllEntries() }

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.getDataOrThrow()).isEqualTo(listOf(ENTRY1))
    }

    @Test
    fun `getAllEntries should return error`() {
        // arrange
        val key = KeepassKey.PasswordKey(DB_PASSWORD)
        val readDbUseCase = mockk<ReadDatabaseUseCase>()
        val exception = IllegalArgumentException()

        every { readDbUseCase.readDatabase(key, DB_PATH) }.returns(Result.Error(exception))

        // act
        val result = GetVisibleEntriesUseCase(readDbUseCase)
            .getEntries(key, DB_PATH)

        // assert
        verify { readDbUseCase.readDatabase(key, DB_PATH) }

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat(result.getExceptionOrThrow()).isEqualTo(exception)
    }
}