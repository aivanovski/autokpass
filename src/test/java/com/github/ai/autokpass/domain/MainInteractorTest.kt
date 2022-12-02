package com.github.ai.autokpass.domain

import com.github.ai.autokpass.TestData.COMMAND
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.KEY_PATH
import com.github.ai.autokpass.domain.arguments.ArgumentExtractor
import com.github.ai.autokpass.domain.arguments.ArgumentParser
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase
import com.github.ai.autokpass.model.InputReaderType
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.screens.select_entry.SelectEntryArgs
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beTheSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.Exception

class MainInteractorTest {

    private val argumentExtractor: ArgumentExtractor = mockk()
    private val argumentParser: ArgumentParser = mockk()
    private val printGreetingsUseCase: PrintGreetingsUseCase = mockk()
    private val interactor = MainInteractor(argumentExtractor, argumentParser, printGreetingsUseCase)

    @Test
    fun `initApp should print greetings`() {
        // arrange
        every { printGreetingsUseCase.printGreetings() }.returns(Unit)
        every { argumentExtractor.extractArguments(ARGS) }.returns(rawArgs())
        every { argumentParser.validateAndParse(rawArgs()) }.returns(Result.Success(parsedArgs()))

        // act
        interactor.initApp(ARGS)

        // assert
        verifySequence {
            printGreetingsUseCase.printGreetings()
            argumentExtractor.extractArguments(ARGS)
            argumentParser.validateAndParse(rawArgs())
        }
    }

    @Test
    fun `initApp should return ParsedArgs`() {
        // arrange
        every { printGreetingsUseCase.printGreetings() }.returns(Unit)
        every { argumentExtractor.extractArguments(ARGS) }.returns(rawArgs())
        every { argumentParser.validateAndParse(rawArgs()) }.returns(Result.Success(parsedArgs()))

        // act
        val result = interactor.initApp(ARGS)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe parsedArgs()
    }

    @Test
    fun `initApp should return error`() {
        // arrange
        val exception = Exception()
        every { printGreetingsUseCase.printGreetings() }.returns(Unit)
        every { argumentExtractor.extractArguments(ARGS) }.returns(rawArgs())
        every { argumentParser.validateAndParse(rawArgs()) }.returns(Result.Error(exception))

        // act
        val result = interactor.initApp(ARGS)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beTheSameInstanceAs(exception)
    }

    @Test
    fun `determineStartScreen should return Unlock screen`() {
        // arrange
        val args = parsedArgs(keyPath = null)

        // act
        val result = interactor.determineStartScreen(args)

        // assert
        result shouldBe Screen.Unlock
    }

    @Test
    fun `determineStartScreen should return SelectEntry screen`() {
        // arrange
        val args = parsedArgs(keyPath = KEY_PATH, keyProcessingCommand = COMMAND)
        val key = KeepassKey.FileKey(file = File(KEY_PATH), processingCommand = COMMAND)

        // act
        val result = interactor.determineStartScreen(args)

        // assert
        result shouldBe Screen.SelectEntry(SelectEntryArgs(key))
    }

    private fun rawArgs(): RawArgs =
        RawArgs(
            filePath = DB_PATH,
            keyPath = null,
            delayInSeconds = null,
            autotypeDelayInMillis = null,
            autotypeType = null,
            inputType = null,
            keyProcessingCommand = null
        )

    private fun parsedArgs(
        keyPath: String? = null,
        keyProcessingCommand: String? = null
    ): ParsedArgs =
        ParsedArgs(
            filePath = DB_PATH,
            keyPath = keyPath,
            delayInSeconds = null,
            autotypeDelayInMillis = null,
            inputReaderType = InputReaderType.STANDARD,
            autotypeType = null,
            keyProcessingCommand = keyProcessingCommand
        )

    companion object {

        private val ARGS = arrayOf("-f", DB_PATH)
    }
}