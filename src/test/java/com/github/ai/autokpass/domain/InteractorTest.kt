package com.github.ai.autokpass.domain

import com.github.ai.autokpass.TestData.COMMAND
import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.ENTRY1
import com.github.ai.autokpass.TestData.KEY_PATH
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory.Companion.DEFAULT_DELAY_BETWEEN_ACTIONS
import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.AwaitWindowChangeUseCase
import com.github.ai.autokpass.domain.usecases.DetermineAutotypeExecutorTypeUseCase
import com.github.ai.autokpass.domain.usecases.GetKeyUseCase
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase
import com.github.ai.autokpass.domain.usecases.SelectEntryUseCase
import com.github.ai.autokpass.domain.usecases.SelectPatternUseCase
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.InputReaderType
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Test
import java.io.File

class InteractorTest {

    private val getKeyUseCase = mockk<GetKeyUseCase>()
    private val selectEntryUseCase = mockk<SelectEntryUseCase>()
    private val selectPatternUseCase = mockk<SelectPatternUseCase>()
    private val awaitWindowUseCase = mockk<AwaitWindowChangeUseCase>()
    private val getOsTypeUseCase = mockk<GetOSTypeUseCase>()
    private val determineAutotypeUseCase = mockk<DetermineAutotypeExecutorTypeUseCase>()
    private val autotypeUseCase = mockk<AutotypeUseCase>()
    private val errorInteractor = ErrorInteractorTestImpl()

    @Test
    fun `run should stop if GetOsTypeUseCase returns error`() {
        // arrange
        val error = newError<OSType>()
        every { getOsTypeUseCase.getOSType() }.returns(error)

        // act
        newInteractor().run(newArgs(filePath = DB_PATH))

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
        }
    }

    @Test
    fun `run should stop if DetermineAutotypeExecutorTypeUseCase returns error`() {
        // arrange
        val error = newError<AutotypeExecutorType>()
        every { getOsTypeUseCase.getOSType() }.returns(OS_TYPE_RESULT)
        every { determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null) }.returns(error)

        // act
        newInteractor().run(newArgs(filePath = DB_PATH))

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
            determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null)
        }
    }

    @Test
    fun `run should stop if GetKeyUseCase returns error`() {
        // arrange
        val error = newError<KeepassKey>()
        every { getOsTypeUseCase.getOSType() }.returns(OS_TYPE_RESULT)
        every { determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null) }
            .returns(AUTOTYPE_EXECUTOR_TYPE_RESULT)
        every { getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null) }.returns(error)

        // act
        newInteractor().run(newArgs(filePath = DB_PATH))

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
            determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null)
            getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null)
        }
    }

    @Test
    fun `run should stop if SelectEntryUseCase returns error`() {
        // arrange
        val error = newError<KeepassEntry?>()
        every { getOsTypeUseCase.getOSType() }.returns(OS_TYPE_RESULT)
        every { determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null) }
            .returns(AUTOTYPE_EXECUTOR_TYPE_RESULT)
        every { getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null) }.returns(GET_KEY_RESULT)
        every { selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH) }.returns(error)

        // act
        newInteractor().run(newArgs(filePath = DB_PATH))

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
            determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null)
            getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null)
            selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH)
        }
    }

    @Test
    fun `run should stop if SelectEntryUseCase returns null`() {
        // arrange
        every { getOsTypeUseCase.getOSType() }.returns(OS_TYPE_RESULT)
        every { determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null) }
            .returns(AUTOTYPE_EXECUTOR_TYPE_RESULT)
        every { getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null) }.returns(GET_KEY_RESULT)
        every { selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH) }.returns(Result.Success(null))

        // act
        newInteractor().run(newArgs(filePath = DB_PATH))

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
            determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null)
            getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null)
            selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH)
        }
    }

    @Test
    fun `run should stop if SelectPatternUseCase returns error`() {
        // arrange
        val error = newError<AutotypePattern?>()
        every { getOsTypeUseCase.getOSType() }.returns(OS_TYPE_RESULT)
        every { determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null) }
            .returns(AUTOTYPE_EXECUTOR_TYPE_RESULT)
        every { getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null) }.returns(GET_KEY_RESULT)
        every { selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH) }.returns(SELECT_ENTRY_RESULT)
        every { selectPatternUseCase.selectPattern(AutotypePattern.ALL) }.returns(error)

        // act
        newInteractor().run(newArgs(filePath = DB_PATH))

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
            determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null)
            getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null)
            selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH)
            selectPatternUseCase.selectPattern(AutotypePattern.ALL)
        }
    }

    @Test
    fun `run should stop if SelectPatternUseCase returns null`() {
        // arrange
        every { getOsTypeUseCase.getOSType() }.returns(OS_TYPE_RESULT)
        every { determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null) }
            .returns(AUTOTYPE_EXECUTOR_TYPE_RESULT)
        every { getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null) }.returns(GET_KEY_RESULT)
        every { selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH) }.returns(SELECT_ENTRY_RESULT)
        every { selectPatternUseCase.selectPattern(AutotypePattern.ALL) }.returns(Result.Success(null))

        // act
        newInteractor().run(newArgs(filePath = DB_PATH))

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
            determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null)
            getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null)
            selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH)
            selectPatternUseCase.selectPattern(AutotypePattern.ALL)
        }
    }

    @Test
    fun `run should call AwaitWindowChangedUseCase`() {
        // arrange
        val args = newArgs(filePath = DB_PATH)
        val error = newError<Unit>()
        every { getOsTypeUseCase.getOSType() }.returns(OS_TYPE_RESULT)
        every { determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null) }
            .returns(AUTOTYPE_EXECUTOR_TYPE_RESULT)
        every { getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null) }.returns(GET_KEY_RESULT)
        every { selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH) }.returns(SELECT_ENTRY_RESULT)
        every { selectPatternUseCase.selectPattern(AutotypePattern.ALL) }.returns(SELECT_PATTERN_RESULT)
        every { awaitWindowUseCase.isAbleToAwaitWindowChanged(OSType.LINUX, AutotypeExecutorType.XDOTOOL) }
            .returns(true)
        every { awaitWindowUseCase.awaitUntilWindowChanged() }.returns(error)

        // act
        newInteractor().run(args)

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
            determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null)
            getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null)
            selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH)
            selectPatternUseCase.selectPattern(AutotypePattern.ALL)
            awaitWindowUseCase.isAbleToAwaitWindowChanged(OSType.LINUX, AutotypeExecutorType.XDOTOOL)
            awaitWindowUseCase.awaitUntilWindowChanged()
        }
    }

    @Test
    fun `run should not call AwaitWindowChangedUseCase`() {
        // arrange
        val args = newArgs(filePath = DB_PATH)
        every { getOsTypeUseCase.getOSType() }.returns(OS_TYPE_RESULT)
        every { determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null) }
            .returns(AUTOTYPE_EXECUTOR_TYPE_RESULT)
        every { getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null) }.returns(GET_KEY_RESULT)
        every { selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH) }.returns(SELECT_ENTRY_RESULT)
        every { selectPatternUseCase.selectPattern(AutotypePattern.ALL) }.returns(SELECT_PATTERN_RESULT)
        every { awaitWindowUseCase.isAbleToAwaitWindowChanged(OSType.LINUX, AutotypeExecutorType.XDOTOOL) }
            .returns(false)
        every { autotypeUseCase.doAutotype(any(), any(), any(), any(), any()) }.returns(Result.Success(Unit))

        // act
        newInteractor().run(args)

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
            determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null)
            getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null)
            selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH)
            selectPatternUseCase.selectPattern(AutotypePattern.ALL)
            awaitWindowUseCase.isAbleToAwaitWindowChanged(OSType.LINUX, AutotypeExecutorType.XDOTOOL)
            autotypeUseCase.doAutotype(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `run should call AwaitWindowChangedUseCase --autotype option is specified`() {
        // arrange
        val args = newArgs(
            filePath = DB_PATH,
            autotypeType = AutotypeExecutorType.XDOTOOL
        )
        every { getOsTypeUseCase.getOSType() }.returns(OS_TYPE_RESULT)
        every { determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, AutotypeExecutorType.XDOTOOL) }
            .returns(AUTOTYPE_EXECUTOR_TYPE_RESULT)
        every { getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null) }.returns(GET_KEY_RESULT)
        every { selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH) }.returns(SELECT_ENTRY_RESULT)
        every { selectPatternUseCase.selectPattern(AutotypePattern.ALL) }.returns(SELECT_PATTERN_RESULT)
        every { awaitWindowUseCase.isAbleToAwaitWindowChanged(OSType.LINUX, AutotypeExecutorType.XDOTOOL) }
            .returns(true)
        every { awaitWindowUseCase.awaitUntilWindowChanged() }.returns(Result.Success(Unit))
        every { autotypeUseCase.doAutotype(any(), any(), any(), any(), any()) }.returns(Result.Success(Unit))

        // act
        newInteractor().run(args)

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
            determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, AutotypeExecutorType.XDOTOOL)
            getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null)
            selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH)
            selectPatternUseCase.selectPattern(AutotypePattern.ALL)
            awaitWindowUseCase.isAbleToAwaitWindowChanged(OSType.LINUX, AutotypeExecutorType.XDOTOOL)
            awaitWindowUseCase.awaitUntilWindowChanged()
            autotypeUseCase.doAutotype(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `run should finish if AutotypeUseCase returns error`() {
        // arrange
        val args = newArgs(filePath = DB_PATH)
        val error = newError<Unit>()
        every { getOsTypeUseCase.getOSType() }.returns(OS_TYPE_RESULT)
        every { determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null) }
            .returns(AUTOTYPE_EXECUTOR_TYPE_RESULT)
        every { getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null) }.returns(GET_KEY_RESULT)
        every { selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH) }.returns(SELECT_ENTRY_RESULT)
        every { selectPatternUseCase.selectPattern(AutotypePattern.ALL) }.returns(SELECT_PATTERN_RESULT)
        every { awaitWindowUseCase.isAbleToAwaitWindowChanged(OSType.LINUX, AutotypeExecutorType.XDOTOOL) }
            .returns(false)
        every {
            autotypeUseCase.doAutotype(
                AutotypeExecutorType.XDOTOOL,
                ENTRY1,
                AutotypePattern.DEFAULT_PATTERN,
                DEFAULT_DELAY_BETWEEN_ACTIONS,
                null
            )
        }.returns(error)

        // act
        newInteractor().run(args)

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
            determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, null)
            getKeyUseCase.getKey(InputReaderType.STANDARD, DB_PATH, null, null)
            selectEntryUseCase.selectEntry(PASSWORD_KEY, DB_PATH)
            selectPatternUseCase.selectPattern(AutotypePattern.ALL)
            awaitWindowUseCase.isAbleToAwaitWindowChanged(OSType.LINUX, AutotypeExecutorType.XDOTOOL)
            autotypeUseCase.doAutotype(
                AutotypeExecutorType.XDOTOOL,
                ENTRY1,
                AutotypePattern.DEFAULT_PATTERN,
                DEFAULT_DELAY_BETWEEN_ACTIONS,
                null
            )
        }
    }

    @Test
    fun `run should pass specified arguments`() {
        // arrange
        val args = newArgs(
            filePath = DB_PATH,
            keyPath = KEY_PATH,
            delayInSeconds = DELAY,
            autotypeDelayInMillis = AUTOTYPE_DELAY,
            inputReaderType = InputReaderType.SECRET,
            autotypeType = AutotypeExecutorType.OSA_SCRIPT,
            keyProcessingCommand = COMMAND
        )
        every { getOsTypeUseCase.getOSType() }.returns(OS_TYPE_RESULT)
        every { determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, AutotypeExecutorType.OSA_SCRIPT) }
            .returns(Result.Success(AutotypeExecutorType.OSA_SCRIPT))
        every {
            getKeyUseCase.getKey(
                InputReaderType.SECRET,
                DB_PATH,
                KEY_PATH,
                COMMAND
            )
        }.returns(Result.Success(FILE_KEY))
        every { selectEntryUseCase.selectEntry(FILE_KEY, DB_PATH) }.returns(SELECT_ENTRY_RESULT)
        every { selectPatternUseCase.selectPattern(AutotypePattern.ALL) }.returns(SELECT_PATTERN_RESULT)
        every {
            awaitWindowUseCase.isAbleToAwaitWindowChanged(
                OSType.LINUX,
                AutotypeExecutorType.OSA_SCRIPT
            )
        }.returns(false)
        every {
            autotypeUseCase.doAutotype(
                AutotypeExecutorType.OSA_SCRIPT,
                ENTRY1,
                AutotypePattern.DEFAULT_PATTERN,
                AUTOTYPE_DELAY,
                DELAY
            )
        }.returns(Result.Success(Unit))

        // act
        newInteractor().run(args)

        // assert
        verifySequence {
            getOsTypeUseCase.getOSType()
            determineAutotypeUseCase.getAutotypeExecutorType(OSType.LINUX, AutotypeExecutorType.OSA_SCRIPT)
            getKeyUseCase.getKey(InputReaderType.SECRET, DB_PATH, KEY_PATH, COMMAND)
            selectEntryUseCase.selectEntry(FILE_KEY, DB_PATH)
            selectPatternUseCase.selectPattern(AutotypePattern.ALL)
            awaitWindowUseCase.isAbleToAwaitWindowChanged(OSType.LINUX, AutotypeExecutorType.OSA_SCRIPT)
            autotypeUseCase.doAutotype(
                AutotypeExecutorType.OSA_SCRIPT,
                ENTRY1,
                AutotypePattern.DEFAULT_PATTERN,
                AUTOTYPE_DELAY,
                DELAY
            )
        }
    }

    private fun newArgs(
        filePath: String,
        keyPath: String? = null,
        delayInSeconds: Long? = null,
        autotypeDelayInMillis: Long? = null,
        inputReaderType: InputReaderType = InputReaderType.STANDARD,
        autotypeType: AutotypeExecutorType? = null,
        keyProcessingCommand: String? = null
    ): ParsedArgs =
        ParsedArgs(
            filePath = filePath,
            keyPath = keyPath,
            delayInSeconds = delayInSeconds,
            autotypeDelayInMillis = autotypeDelayInMillis,
            inputReaderType = inputReaderType,
            autotypeType = autotypeType,
            keyProcessingCommand = keyProcessingCommand
        )

    private fun newInteractor(
        getKeyUseCase: GetKeyUseCase = this.getKeyUseCase,
        selectEntryUseCase: SelectEntryUseCase = this.selectEntryUseCase,
        selectPatternUseCase: SelectPatternUseCase = this.selectPatternUseCase,
        awaitWindowUseCase: AwaitWindowChangeUseCase = this.awaitWindowUseCase,
        getOsTypeUseCase: GetOSTypeUseCase = this.getOsTypeUseCase,
        determineAutotypeUseCase: DetermineAutotypeExecutorTypeUseCase = this.determineAutotypeUseCase,
        autotypeUseCase: AutotypeUseCase = this.autotypeUseCase,
        errorInteractor: ErrorInteractor = this.errorInteractor
    ): Interactor =
        Interactor(
            getKeyUseCase,
            selectEntryUseCase,
            selectPatternUseCase,
            awaitWindowUseCase,
            getOsTypeUseCase,
            determineAutotypeUseCase,
            autotypeUseCase,
            errorInteractor
        )

    private fun <T> newError(): Result<T> {
        return Result.Error(Exception())
    }

    companion object {
        private const val DELAY = 123L
        private const val AUTOTYPE_DELAY = 456L

        private val PASSWORD_KEY = KeepassKey.PasswordKey(DB_PASSWORD)
        private val FILE_KEY = KeepassKey.FileKey(File(KEY_PATH))

        private val OS_TYPE_RESULT = Result.Success(OSType.LINUX)
        private val AUTOTYPE_EXECUTOR_TYPE_RESULT = Result.Success(AutotypeExecutorType.XDOTOOL)
        private val GET_KEY_RESULT = Result.Success(PASSWORD_KEY)
        private val SELECT_ENTRY_RESULT = Result.Success(ENTRY1)
        private val SELECT_PATTERN_RESULT = Result.Success(AutotypePattern.DEFAULT_PATTERN)
    }
}