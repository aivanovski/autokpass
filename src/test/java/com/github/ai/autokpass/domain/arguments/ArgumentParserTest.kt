package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.extensions.getDefaultAsLong
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.lang.String.format

class ArgumentParserTest {

    private val strings: StringResources = StringResourcesImpl()

    @Test
    fun `validateAndParse should return result if --file exists`() {
        // arrange
        val args = argsWith(filePath = FILE_PATH)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
    }

    @Test
    fun `validateAndParse should return error if --file is null`() {
        // arrange
        val args = argsWith(filePath = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe(
            format(
                strings.errorOptionCanNotBeEmpty,
                Argument.FILE.cliName
            )
        )
    }

    @Test
    fun `validateAndParse should return error if --file is empty`() {
        // arrange
        val args = argsWith(filePath = EMPTY)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe(
            format(
                strings.errorOptionCanNotBeEmpty,
                Argument.FILE.cliName
            )
        )
    }

    @Test
    fun `validateAndParse should return error if --file doesn't exist`() {
        // arrange
        val args = argsWith(filePath = FILE_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        every { fsProvider.exists(FILE_PATH) }.returns(false)

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe(format(strings.errorFileDoesNotExist, FILE_PATH))
    }

    @Test
    fun `validateAndParse should return error if --file is a directory`() {
        // arrange
        val args = argsWith(filePath = FILE_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        every { fsProvider.exists(FILE_PATH) }.returns(true)
        every { fsProvider.isFile(FILE_PATH) }.returns(false)

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe(format(strings.errorFileIsNotFile, FILE_PATH))
    }

    @Test
    fun `validateAndParse should return result if --key-file exists`() {
        // arrange
        val args = argsWith(keyPath = KEY_PATH)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe args.toParsedArgs()
    }

    @Test
    fun `validateAndParse should return result if --key-file is null`() {
        // arrange
        val args = argsWith(keyPath = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe args.toParsedArgs()
    }

    @Test
    fun `validateAndParse should return error if --key-file is empty`() {
        // arrange
        val args = argsWith(keyPath = EMPTY)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe(
            format(
                strings.errorOptionCanNotBeEmpty,
                Argument.KEY_FILE.cliName
            )
        )
    }

    @Test
    fun `validateAndParse should return error if --key-file doesn't exist`() {
        // arrange
        val args = argsWith(keyPath = KEY_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        every { fsProvider.exists(FILE_PATH) }.returns(true)
        every { fsProvider.isFile(FILE_PATH) }.returns(true)
        every { fsProvider.exists(KEY_PATH) }.returns(false)

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe(format(strings.errorFileDoesNotExist, KEY_PATH))
    }

    @Test
    fun `validateAndParse should return error if --key-file is a directory`() {
        // arrange
        val args = argsWith(filePath = KEY_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        every { fsProvider.exists(FILE_PATH) }.returns(true)
        every { fsProvider.isFile(FILE_PATH) }.returns(true)
        every { fsProvider.exists(KEY_PATH) }.returns(true)
        every { fsProvider.isFile(KEY_PATH) }.returns(false)

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe(format(strings.errorFileIsNotFile, KEY_PATH))
    }

    @Test
    fun `validateAndParse should return autotype delay if --autotype-delay specified in seconds`() {
        // arrange
        val args = argsWith(delayBetweenActions = DELAY_IN_SECONDS)
        val expectedArgs = args.toParsedArgs(
            delayBetweenActionsInMillis = DELAY_IN_SECONDS.toLong() * 1000L
        )

        // act
        val result = ArgumentParser(providerForAnyFile(), strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedArgs
    }

    @Test
    fun `validateAndParse should return autotype delay if --autotype-delay specified in milliseconds`() {
        // arrange
        val args = argsWith(delayBetweenActions = DELAY_IN_MILLISECONDS)
        val expectedArgs = args.toParsedArgs(
            delayBetweenActionsInMillis = DELAY_IN_MILLISECONDS.toLong()
        )

        // act
        val result = ArgumentParser(providerForAnyFile(), strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedArgs
    }

    @Test
    fun `validateAndParse should return null if --autotype-delay is null`() {
        // arrange
        val args = argsWith(delayBetweenActions = null)

        // act
        val result = ArgumentParser(providerForAnyFile(), strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe args.toParsedArgs()
    }

    @Test
    fun `validateAndParse should return null if --autotype-delay is empty`() {
        // arrange
        val args = argsWith(delayBetweenActions = EMPTY)

        // act
        val result = ArgumentParser(providerForAnyFile(), strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe(args.copy(delayBetweenActions = null).toParsedArgs())
    }

    @Test
    fun `validateAndParse should return error if --autotype-delay is invalid`() {
        // arrange
        val args = argsWith(delayBetweenActions = INVALID_VALUE)

        // act
        val result = ArgumentParser(providerForAnyFile(), strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe(
            format(
                strings.errorFailedToParseArgument,
                Argument.AUTOTYPE_DELAY.cliName,
                INVALID_VALUE
            )
        )
    }

    @Test
    fun `validateAndParse should return delay if --delay specified in seconds`() {
        // arrange
        val args = argsWith(startDelay = DELAY_IN_SECONDS)
        val fsProvider = providerForAnyFile()
        val expectedArgs = args.toParsedArgs(
            startDelayInMillis = DELAY_IN_SECONDS.toLong() * 1000L
        )

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedArgs
    }

    @Test
    fun `validateAndParse should return delay if --delay specified in milliseconds`() {
        // arrange
        val args = argsWith(startDelay = DELAY_IN_MILLISECONDS)
        val fsProvider = providerForAnyFile()
        val expectedArgs = args.toParsedArgs(
            startDelayInMillis = DELAY_IN_MILLISECONDS.toLong()
        )

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedArgs
    }

    @Test
    fun `validateAndParse should return default value if --delay is null`() {
        // arrange
        val args = argsWith(startDelay = null)
        val expectedArgs = args.toParsedArgs(
            startDelayInMillis = Argument.DELAY.getDefaultAsLong()
        )
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedArgs
    }

    @Test
    fun `validateAndParse should return default value if --delay is empty`() {
        // arrange
        val args = argsWith(startDelay = EMPTY)
        val expectedArgs = args.toParsedArgs(
            startDelayInMillis = Argument.DELAY.getDefaultAsLong()
        )
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedArgs
    }

    @Test
    fun `validateAndParse should return error if --delay is invalid`() {
        // arrange
        val args = argsWith(startDelay = INVALID_VALUE)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe(
            format(
                strings.errorFailedToParseArgument,
                Argument.DELAY.cliName,
                INVALID_VALUE
            )
        )
    }

    @Test
    fun `validateAndParse should return null if --autotype is not specified`() {
        // arrange
        val args = argsWith(autotypeExecutorType = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe args.toParsedArgs()
    }

    @Test
    fun `validateAndParse should return value if --autotype is specified`() {
        // arrange
        val args = argsWith(autotypeExecutorType = AutotypeExecutorType.XDOTOOL.cliName)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe args.toParsedArgs()
    }

    @Test
    fun `validateAndParse should return error if --autotype is invalid`() {
        // arrange
        val args = argsWith(autotypeExecutorType = INVALID_VALUE)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe(
            format(
                strings.errorFailedToParseArgument,
                Argument.AUTOTYPE.cliName,
                INVALID_VALUE
            )
        )
    }

    @Test
    fun `validateAndParse should return error if all empty`() {
        // arrange
        val args = argsWith(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe strings.errorNoArgumentsWereSpecified
    }

    @Test
    fun `validateAndParse should return error if all null`() {
        // arrange
        val args = argsWith(null, null, null, null, null, null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe strings.errorNoArgumentsWereSpecified
    }

    @Test
    fun `validateAndParse should return value if --process-key-command is specified`() {
        // arrange
        val args = argsWith(keyProcessingCommand = COMMAND)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe args.toParsedArgs()
    }

    @Test
    fun `validateAndParse should return value if --process-key-command is not specified`() {
        // arrange
        val args = argsWith(keyProcessingCommand = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe args.toParsedArgs()
    }

    @Test
    fun `validateAndParse should return error if --process-key-command is empty`() {
        // arrange
        val args = argsWith(keyProcessingCommand = EMPTY)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider, strings).validateAndParse(args)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe(
            format(strings.errorOptionCanNotBeEmpty, Argument.PROCESS_KEY_COMMAND.cliName)
        )
    }

    private fun argsWith(
        filePath: String? = FILE_PATH,
        keyPath: String? = null,
        startDelay: String? = Argument.DELAY.defaultValue,
        delayBetweenActions: String? = Argument.AUTOTYPE_DELAY.defaultValue,
        autotypeExecutorType: String? = AutotypeExecutorType.XDOTOOL.cliName,
        keyProcessingCommand: String? = null
    ): RawArgs {
        return RawArgs(
            filePath = filePath,
            keyPath = keyPath,
            startDelay = startDelay,
            delayBetweenActions = delayBetweenActions,
            autotypeType = autotypeExecutorType,
            keyProcessingCommand = keyProcessingCommand
        )
    }

    private fun RawArgs.toParsedArgs(
        startDelayInMillis: Long = startDelay?.toLong() ?: Argument.DELAY.getDefaultAsLong(),
        delayBetweenActionsInMillis: Long = delayBetweenActions?.toLong() ?: Argument.AUTOTYPE_DELAY.getDefaultAsLong()
    ): ParsedArgs =
        ParsedArgs(
            filePath = filePath ?: EMPTY,
            keyPath = keyPath,
            startDelayInMillis = startDelayInMillis,
            delayBetweenActionsInMillis = delayBetweenActionsInMillis,
            autotypeType = AutotypeExecutorType.values().firstOrNull { it.cliName == autotypeType },
            keyProcessingCommand = keyProcessingCommand
        )

    private fun providerForAnyFile(): FileSystemProvider {
        val provider = mockk<FileSystemProvider>()

        every { provider.exists(any()) }.returns(true)
        every { provider.isFile(any()) }.returns(true)

        return provider
    }

    companion object {
        private const val FILE_PATH = "/tmp/filePath"
        private const val KEY_PATH = "/tmp/keyPath"
        private const val DELAY_IN_SECONDS = "8"
        private const val DELAY_IN_MILLISECONDS = "1550"
        private const val INVALID_VALUE = "abc123/_=[]"
        private const val COMMAND = "gpg --decrypt"
    }
}