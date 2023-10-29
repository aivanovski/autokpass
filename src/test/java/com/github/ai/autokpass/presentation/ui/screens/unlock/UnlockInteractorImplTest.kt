package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.TestData.COMMAND
import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.DEFAULT_DELAY
import com.github.ai.autokpass.TestData.DEFAULT_DELAY_BETWEEN_ACTIONS
import com.github.ai.autokpass.TestData.EXCEPTION
import com.github.ai.autokpass.TestData.KEY_PATH
import com.github.ai.autokpass.data.config.ConfigRepository
import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.domain.usecases.ReadDatabaseUseCase
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.utils.TestDispatchers
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class UnlockInteractorImplTest {

    private val dispatchers = TestDispatchers()
    private val readDatabaseUseCase = mockk<ReadDatabaseUseCase>()
    private val configRepository = mockk<ConfigRepository>()

    @Test
    fun `unlockDatabase should return result`() = runTest(UnconfinedTestDispatcher()) {
        KEYS.forEach { key ->
            // arrange
            val db = mockk<KeepassDatabase>()
            every {
                readDatabaseUseCase.readDatabase(key, DB_PATH)
            }.returns(Result.Success(db))

            // act
            val result = newInteractor().unlockDatabase(key, DB_PATH)

            // assert
            result shouldBe Result.Success(Unit)
        }
    }

    @Test
    fun `unlockDatabase should return error`() = runTest(UnconfinedTestDispatcher()) {
        KEYS.forEach { key ->
            // arrange
            val expected = Result.Error(EXCEPTION)
            every { readDatabaseUseCase.readDatabase(key, DB_PATH) }.returns(expected)

            // act
            val result = newInteractor().unlockDatabase(key, DB_PATH)

            // assert
            result shouldBe expected
        }
    }

    @Test
    fun `loadConfig should call repository`() = runTest(UnconfinedTestDispatcher()) {
        listOf(
            Result.Success(newConfig()),
            Result.Error(EXCEPTION)
        ).forEach { expected ->
            // arrange
            every { configRepository.getCurrent() }.returns(expected)

            // act
            val result = newInteractor().loadConfig()

            // assert
            result shouldBe expected
        }
    }

    private fun newInteractor(): UnlockInteractorImpl =
        UnlockInteractorImpl(
            dispatchers = dispatchers,
            readDatabaseUseCase = readDatabaseUseCase,
            configRepository = configRepository
        )

    private fun newConfig(): ParsedConfig =
        ParsedConfig(
            filePath = DB_PATH,
            keyPath = null,
            startDelayInMillis = DEFAULT_DELAY,
            delayBetweenActionsInMillis = DEFAULT_DELAY_BETWEEN_ACTIONS,
            autotypeType = null,
            keyProcessingCommand = null
        )

    companion object {
        private val KEYS = listOf(
            KeepassKey.PasswordKey(DB_PASSWORD),
            KeepassKey.FileKey(File(KEY_PATH), COMMAND)
        )
    }
}