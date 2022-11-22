package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.TestData.COMMAND
import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.KEY_PATH
import com.github.ai.autokpass.model.InputReaderType
import com.github.ai.autokpass.model.KeepassKey.FileKey
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.model.Result
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beTheSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import java.io.File

class GetKeyUseCaseTest {

    private val readPasswordUseCase = mockk<ReadPasswordUseCase>()

    @Test
    fun `getKey should return password`() {
        // arrange
        every { readPasswordUseCase.readPassword(InputReaderType.STANDARD, DB_PATH) }
            .returns(Result.Success(DB_PASSWORD))

        // act
        val result = GetKeyUseCase(readPasswordUseCase)
            .getKey(
                inputReaderType = InputReaderType.STANDARD,
                dbFilePath = DB_PATH,
                keyPath = null,
                keyProcessingCommand = null
            )

        // assert
        verifySequence { readPasswordUseCase.readPassword(InputReaderType.STANDARD, DB_PATH) }
        result shouldBe Result.Success(PasswordKey(DB_PASSWORD))
    }

    @Test
    fun `getKey should return error if unable to read password`() {
        // arrange
        val exception = Exception()
        every { readPasswordUseCase.readPassword(InputReaderType.STANDARD, DB_PATH) }
            .returns(Result.Error(exception))

        // act
        val result = GetKeyUseCase(readPasswordUseCase)
            .getKey(
                inputReaderType = InputReaderType.STANDARD,
                dbFilePath = DB_PATH,
                keyPath = null,
                keyProcessingCommand = null
            )

        // assert
        verifySequence { readPasswordUseCase.readPassword(InputReaderType.STANDARD, DB_PATH) }
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beTheSameInstanceAs(exception)
    }

    @Test
    fun `getKey should return key file`() {
        // arrange
        val expected = Result.Success(
            FileKey(
                file = File(KEY_PATH),
                processingCommand = COMMAND
            )
        )

        // act
        val result = GetKeyUseCase(readPasswordUseCase)
            .getKey(
                inputReaderType = InputReaderType.STANDARD,
                dbFilePath = DB_PATH,
                keyPath = KEY_PATH,
                keyProcessingCommand = COMMAND
            )

        // assert
        result shouldBe expected
    }
}