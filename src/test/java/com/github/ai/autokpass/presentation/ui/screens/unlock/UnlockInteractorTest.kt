package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.ERROR_MESSAGE
import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.domain.usecases.ReadDatabaseUseCase
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.utils.TestDispatchers
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class UnlockInteractorTest {

    private val dispatchers = TestDispatchers()
    private val readDatabaseUseCase = mockk<ReadDatabaseUseCase>()

    @Test
    fun `unlockDatabase should return result`() = runTest(UnconfinedTestDispatcher()) {
        // arrange
        val db = mockk<KeepassDatabase>()
        every { readDatabaseUseCase.readDatabase(PasswordKey(DB_PASSWORD), DB_PATH) }.returns(Result.Success(db))

        // act
        val result = interactor().unlockDatabase(DB_PASSWORD, DB_PATH)

        // assert
        result shouldBe Result.Success(Unit)
    }

    @Test
    fun `unlockDatabase should return error`() = runTest(UnconfinedTestDispatcher()) {
        // arrange
        val exception = Exception(ERROR_MESSAGE)
        every { readDatabaseUseCase.readDatabase(PasswordKey(DB_PASSWORD), DB_PATH) }.returns(Result.Error(exception))

        // act
        val result = interactor().unlockDatabase(DB_PASSWORD, DB_PATH)

        // assert
        result shouldBe Result.Error(exception)
    }

    private fun interactor(): UnlockInteractor =
        UnlockInteractor(
            dispatchers = dispatchers,
            readDatabaseUseCase = readDatabaseUseCase
        )
}