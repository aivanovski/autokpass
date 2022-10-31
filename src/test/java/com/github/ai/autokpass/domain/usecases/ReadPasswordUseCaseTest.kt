package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.INVALID_DB_PASSWORD
import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.domain.Errors
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.exception.InvalidPasswordException
import com.github.ai.autokpass.domain.usecases.ReadPasswordUseCase.Companion.ENTER_PASSWORD_MESSAGE
import com.github.ai.autokpass.model.InputReaderType
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.input.InputReader
import com.github.ai.autokpass.presentation.input.InputReaderFactory
import com.github.ai.autokpass.presentation.printer.Printer
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.beTheSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import java.lang.Exception

class ReadPasswordUseCaseTest {

    private val readDatabaseUseCase = mockk<ReadDatabaseUseCase>()
    private val printer = mockk<Printer>()
    private val inputReaderFactory = mockk<InputReaderFactory>()
    private val inputReader = mockk<InputReader>()

    @Test
    fun `readPassword should return password if it is valid`() {
        // arrange
        val db = mockk<KeepassDatabase>()
        val key = PasswordKey(DB_PASSWORD)
        every { printer.println(ENTER_PASSWORD_MESSAGE) }.returns(Unit)
        every { inputReaderFactory.getInputReader(InputReaderType.STANDARD) }.returns(inputReader)
        every { inputReader.read() }.returns(DB_PASSWORD)
        every { readDatabaseUseCase.readDatabase(key, DB_PATH) }.returns(Result.Success(db))

        // act
        val result = createUseCase().readPassword(InputReaderType.STANDARD, DB_PATH)

        // assert
        verifySequence {
            printer.println(ENTER_PASSWORD_MESSAGE)
            inputReaderFactory.getInputReader(InputReaderType.STANDARD)
            inputReader.read()
            readDatabaseUseCase.readDatabase(key, DB_PATH)
        }

        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe DB_PASSWORD
    }

    @Test
    fun `readPassword should return error if ReadDatabaseUseCase is unable to read db`() {
        // arrange
        val exception = Exception()
        val key = PasswordKey(DB_PASSWORD)
        every { printer.println(ENTER_PASSWORD_MESSAGE) }.returns(Unit)
        every { inputReaderFactory.getInputReader(InputReaderType.STANDARD) }.returns(inputReader)
        every { inputReader.read() }.returns(DB_PASSWORD)
        every { readDatabaseUseCase.readDatabase(key, DB_PATH) }.returns(Result.Error(exception))

        // act
        val result = createUseCase().readPassword(InputReaderType.STANDARD, DB_PATH)

        // assert
        verifySequence {
            printer.println(ENTER_PASSWORD_MESSAGE)
            inputReaderFactory.getInputReader(InputReaderType.STANDARD)
            inputReader.read()
            readDatabaseUseCase.readDatabase(key, DB_PATH)
        }

        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beTheSameInstanceAs(exception)
    }

    @Test
    fun `readPassword should ask password three times and return error`() {
        // arrange
        val exception = InvalidPasswordException()
        val key = PasswordKey(DB_PASSWORD)
        every { printer.println(ENTER_PASSWORD_MESSAGE) }.returns(Unit)
        every { printer.println(Errors.INVALID_PASSWORD_MESSAGE) }.returns(Unit)
        every { inputReaderFactory.getInputReader(InputReaderType.STANDARD) }.returns(inputReader)
        every { inputReader.read() }.returns(DB_PASSWORD)
        every { readDatabaseUseCase.readDatabase(key, DB_PATH) }.returns(Result.Error(exception))

        // act
        val result = createUseCase().readPassword(InputReaderType.STANDARD, DB_PATH)

        // assert
        verifySequence {
            printer.println(ENTER_PASSWORD_MESSAGE)
            inputReaderFactory.getInputReader(InputReaderType.STANDARD)
            inputReader.read()
            readDatabaseUseCase.readDatabase(key, DB_PATH)
            printer.println(Errors.INVALID_PASSWORD_MESSAGE)
            inputReaderFactory.getInputReader(InputReaderType.STANDARD)
            inputReader.read()
            readDatabaseUseCase.readDatabase(key, DB_PATH)
            printer.println(Errors.INVALID_PASSWORD_MESSAGE)
            inputReaderFactory.getInputReader(InputReaderType.STANDARD)
            inputReader.read()
            readDatabaseUseCase.readDatabase(key, DB_PATH)
        }

        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<AutokpassException>()
        result.getExceptionOrThrow().message shouldBe Errors.TOO_MANY_ATTEMPTS
    }

    @Test
    fun `readPassword should return password if first attempt failed`() {
        // arrange
        val db = mockk<KeepassDatabase>()
        val exception = InvalidPasswordException()
        val invalidKey = PasswordKey(INVALID_DB_PASSWORD)
        val key = PasswordKey(DB_PASSWORD)
        every { printer.println(ENTER_PASSWORD_MESSAGE) }.returns(Unit)
        every { printer.println(Errors.INVALID_PASSWORD_MESSAGE) }.returns(Unit)
        every { inputReaderFactory.getInputReader(InputReaderType.STANDARD) }.returns(inputReader)
        every { inputReader.read() }.returns(INVALID_DB_PASSWORD).andThen(DB_PASSWORD)
        every { readDatabaseUseCase.readDatabase(key, DB_PATH) }.returns(Result.Success(db))
        every { readDatabaseUseCase.readDatabase(invalidKey, DB_PATH) }.returns(Result.Error(exception))

        // act
        val result = createUseCase().readPassword(InputReaderType.STANDARD, DB_PATH)

        // assert
        verifySequence {
            printer.println(ENTER_PASSWORD_MESSAGE)
            inputReaderFactory.getInputReader(InputReaderType.STANDARD)
            inputReader.read()
            readDatabaseUseCase.readDatabase(invalidKey, DB_PATH)
            printer.println(Errors.INVALID_PASSWORD_MESSAGE)
            inputReaderFactory.getInputReader(InputReaderType.STANDARD)
            inputReader.read()
            readDatabaseUseCase.readDatabase(key, DB_PATH)
        }

        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe DB_PASSWORD
    }

    private fun createUseCase(): ReadPasswordUseCase {
        return ReadPasswordUseCase(
            readDatabaseUseCase = readDatabaseUseCase,
            printer = printer,
            inputReaderFactory = inputReaderFactory
        )
    }
}