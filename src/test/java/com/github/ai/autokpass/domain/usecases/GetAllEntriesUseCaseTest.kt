package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.ENTRIES
import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class GetAllEntriesUseCaseTest {

    @Test
    fun `getAllEntries should return all entries from db`() {
        // arrange
        val key = KeepassKey.PasswordKey(DB_PASSWORD)
        val readDbUseCase = mockk<ReadDatabaseUseCase>()
        val db = mockk<KeepassDatabase>()

        every { readDbUseCase.readDatabase(key, DB_PATH) }.returns(Result.Success(db))
        every { db.getAllEntries() }.returns(ENTRIES)

        // act
        val result = GetAllEntriesUseCase(readDbUseCase)
            .getAllEntries(key, DB_PATH)

        // assert
        verify { readDbUseCase.readDatabase(key, DB_PATH) }
        verify { db.getAllEntries() }
        confirmVerified()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.getDataOrThrow()).isEqualTo(ENTRIES)
    }

    @Test
    fun `getAllEntries should return error`() {
        // arrange
        val key = KeepassKey.PasswordKey(DB_PASSWORD)
        val readDbUseCase = mockk<ReadDatabaseUseCase>()
        val exception = IllegalArgumentException()

        every { readDbUseCase.readDatabase(key, DB_PATH) }.returns(Result.Error(exception))

        // act
        val result = GetAllEntriesUseCase(readDbUseCase)
            .getAllEntries(key, DB_PATH)

        // assert
        verify { readDbUseCase.readDatabase(key, DB_PATH) }
        confirmVerified()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat(result.getExceptionOrThrow()).isEqualTo(exception)
    }
}