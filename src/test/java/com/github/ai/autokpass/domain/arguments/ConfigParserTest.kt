package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.exception.EmptyConfigException
import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.extensions.getDefaultAsLong
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.RawConfig
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class ConfigParserTest {

    private val strings: StringResources = StringResourcesImpl()

    @Test
    fun `validateAndParse should return result if --file exists`() {
        // arrange
        val config = newConfig(filePath = FILE_PATH)
        val fsProvider = providerForAnyFile()

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
    }

    @Test
    fun `validateAndParse should return error if --file is null`() {
        // arrange
        val config = newConfig(filePath = null)
        val fsProvider = providerForAnyFile()
        val expectedMessage = String.format(
            strings.errorOptionCanNotBeEmpty,
            Argument.FILE.cliName
        )

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `validateAndParse should return error if --file is empty`() {
        // arrange
        val config = newConfig(filePath = EMPTY)
        val fsProvider = providerForAnyFile()
        val expectedMessage = String.format(
            strings.errorOptionCanNotBeEmpty,
            Argument.FILE.cliName
        )

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `validateAndParse should return error if --file doesn't exist`() {
        // arrange
        val config = newConfig(filePath = FILE_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        val expectedMessage = String.format(
            strings.errorFileDoesNotExist,
            FILE_PATH
        )

        every { fsProvider.exists(FILE_PATH) }.returns(false)

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `validateAndParse should return error if --file is a directory`() {
        // arrange
        val config = newConfig(filePath = FILE_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        val expectedMessage = String.format(strings.errorFileIsNotFile, FILE_PATH)
        every { fsProvider.exists(FILE_PATH) }.returns(true)
        every { fsProvider.isFile(FILE_PATH) }.returns(false)

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `validateAndParse should return result if --key-file exists`() {
        // arrange
        val config = newConfig(keyPath = KEY_PATH)
        val fsProvider = providerForAnyFile()

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe config.toParsedConfig()
    }

    @Test
    fun `validateAndParse should return result if --key-file is null`() {
        // arrange
        val config = newConfig(keyPath = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe config.toParsedConfig()
    }

    @Test
    fun `validateAndParse should return error if --key-file is empty`() {
        // arrange
        val config = newConfig(keyPath = EMPTY)
        val fsProvider = providerForAnyFile()
        val expectedMessage = String.format(
            strings.errorOptionCanNotBeEmpty,
            Argument.KEY_FILE.cliName
        )

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `validateAndParse should return error if --key-file doesn't exist`() {
        // arrange
        val config = newConfig(keyPath = KEY_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        val expectedMessage = String.format(
            strings.errorFileDoesNotExist,
            KEY_PATH
        )
        every { fsProvider.exists(FILE_PATH) }.returns(true)
        every { fsProvider.isFile(FILE_PATH) }.returns(true)
        every { fsProvider.exists(KEY_PATH) }.returns(false)

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `validateAndParse should return error if --key-file is a directory`() {
        // arrange
        val config = newConfig(filePath = KEY_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        val expectedMessage = String.format(strings.errorFileIsNotFile, KEY_PATH)

        every { fsProvider.exists(FILE_PATH) }.returns(true)
        every { fsProvider.isFile(FILE_PATH) }.returns(true)
        every { fsProvider.exists(KEY_PATH) }.returns(true)
        every { fsProvider.isFile(KEY_PATH) }.returns(false)

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `validateAndParse should return autotype delay if --autotype-delay specified in seconds`() {
        // arrange
        val config = newConfig(delayBetweenActions = DELAY_IN_SECONDS)
        val expectedConfig = config.toParsedConfig(
            delayBetweenActionsInMillis = DELAY_IN_SECONDS.toLong() * 1000L
        )

        // act
        val result = ConfigParser(providerForAnyFile(), strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedConfig
    }

    @Test
    fun `validateAndParse should return autotype delay if --autotype-delay specified in millis`() {
        // arrange
        val config = newConfig(delayBetweenActions = DELAY_IN_MILLISECONDS)
        val expectedConfig = config.toParsedConfig(
            delayBetweenActionsInMillis = DELAY_IN_MILLISECONDS.toLong()
        )

        // act
        val result = ConfigParser(providerForAnyFile(), strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedConfig
    }

    @Test
    fun `validateAndParse should return null if --autotype-delay is null`() {
        // arrange
        val config = newConfig(delayBetweenActions = null)

        // act
        val result = ConfigParser(providerForAnyFile(), strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe config.toParsedConfig()
    }

    @Test
    fun `validateAndParse should return null if --autotype-delay is empty`() {
        // arrange
        val config = newConfig(delayBetweenActions = EMPTY)

        // act
        val result = ConfigParser(providerForAnyFile(), strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe (config.copy(delayBetweenActions = null).toParsedConfig())
    }

    @Test
    fun `validateAndParse should return error if --autotype-delay is invalid`() {
        // arrange
        val config = newConfig(delayBetweenActions = INVALID_VALUE)
        val expectedMessage = String.format(
            strings.errorFailedToParseArgument,
            Argument.AUTOTYPE_DELAY.cliName,
            INVALID_VALUE
        )

        // act
        val result = ConfigParser(providerForAnyFile(), strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `validateAndParse should return delay if --delay specified in seconds`() {
        // arrange
        val config = newConfig(startDelay = DELAY_IN_SECONDS)
        val fsProvider = providerForAnyFile()
        val expectedConfig = config.toParsedConfig(
            startDelayInMillis = DELAY_IN_SECONDS.toLong() * 1000L
        )

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedConfig
    }

    @Test
    fun `validateAndParse should return delay if --delay specified in milliseconds`() {
        // arrange
        val config = newConfig(startDelay = DELAY_IN_MILLISECONDS)
        val fsProvider = providerForAnyFile()
        val expectedConfig = config.toParsedConfig(
            startDelayInMillis = DELAY_IN_MILLISECONDS.toLong()
        )

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedConfig
    }

    @Test
    fun `validateAndParse should return default value if --delay is null`() {
        // arrange
        val config = newConfig(startDelay = null)
        val expectedConfig = config.toParsedConfig(
            startDelayInMillis = Argument.DELAY.getDefaultAsLong()
        )
        val fsProvider = providerForAnyFile()

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedConfig
    }

    @Test
    fun `validateAndParse should return default value if --delay is empty`() {
        // arrange
        val config = newConfig(startDelay = EMPTY)
        val expectedConfig = config.toParsedConfig(
            startDelayInMillis = Argument.DELAY.getDefaultAsLong()
        )
        val fsProvider = providerForAnyFile()

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe expectedConfig
    }

    @Test
    fun `validateAndParse should return error if --delay is invalid`() {
        // arrange
        val config = newConfig(startDelay = INVALID_VALUE)
        val fsProvider = providerForAnyFile()
        val expectedMessage = String.format(
            strings.errorFailedToParseArgument,
            Argument.DELAY.cliName,
            INVALID_VALUE
        )

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `validateAndParse should return null if --autotype is not specified`() {
        // arrange
        val config = newConfig(autotypeExecutorType = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe config.toParsedConfig()
    }

    @Test
    fun `validateAndParse should return value if --autotype is specified`() {
        // arrange
        val config = newConfig(autotypeExecutorType = AutotypeExecutorType.XDOTOOL.cliName)
        val fsProvider = providerForAnyFile()

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe config.toParsedConfig()
    }

    @Test
    fun `validateAndParse should return error if --autotype is invalid`() {
        // arrange
        val config = newConfig(autotypeExecutorType = INVALID_VALUE)
        val fsProvider = providerForAnyFile()
        val expectedMessage = String.format(
            strings.errorFailedToParseArgument,
            Argument.AUTOTYPE.cliName,
            INVALID_VALUE
        )

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `validateAndParse should return error if all empty`() {
        // arrange
        val config = newConfig(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY)
        val fsProvider = providerForAnyFile()

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<EmptyConfigException>()
    }

    @Test
    fun `validateAndParse should return error if all null`() {
        // arrange
        val config = newConfig(null, null, null, null, null, null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<EmptyConfigException>()
    }

    @Test
    fun `validateAndParse should return value if --process-key-command is specified`() {
        // arrange
        val config = newConfig(keyProcessingCommand = COMMAND)
        val fsProvider = providerForAnyFile()

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe config.toParsedConfig()
    }

    @Test
    fun `validateAndParse should return value if --process-key-command is not specified`() {
        // arrange
        val config = newConfig(keyProcessingCommand = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe config.toParsedConfig()
    }

    @Test
    fun `validateAndParse should return error if --process-key-command is empty`() {
        // arrange
        val config = newConfig(keyProcessingCommand = EMPTY)
        val fsProvider = providerForAnyFile()
        val expectedMessage = String.format(
            strings.errorOptionCanNotBeEmpty,
            Argument.PROCESS_KEY_COMMAND.cliName
        )

        // act
        val result = ConfigParser(fsProvider, strings).validateAndParse(config)

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    private fun newConfig(
        filePath: String? = FILE_PATH,
        keyPath: String? = null,
        startDelay: String? = Argument.DELAY.defaultValue,
        delayBetweenActions: String? = Argument.AUTOTYPE_DELAY.defaultValue,
        autotypeExecutorType: String? = AutotypeExecutorType.XDOTOOL.cliName,
        keyProcessingCommand: String? = null
    ): RawConfig {
        return RawConfig(
            filePath = filePath,
            keyPath = keyPath,
            startDelay = startDelay,
            delayBetweenActions = delayBetweenActions,
            autotypeType = autotypeExecutorType,
            keyProcessingCommand = keyProcessingCommand
        )
    }

    private fun RawConfig.toParsedConfig(
        startDelayInMillis: Long = startDelay?.toLong()
            ?: Argument.DELAY.getDefaultAsLong(),
        delayBetweenActionsInMillis: Long = delayBetweenActions?.toLong()
            ?: Argument.AUTOTYPE_DELAY.getDefaultAsLong()
    ): ParsedConfig =
        ParsedConfig(
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