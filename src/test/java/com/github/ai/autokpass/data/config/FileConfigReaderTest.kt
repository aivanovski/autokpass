package com.github.ai.autokpass.data.config

import com.github.ai.autokpass.TestData.COMMAND
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.DEFAULT_DELAY
import com.github.ai.autokpass.TestData.DEFAULT_DELAY_BETWEEN_ACTIONS
import com.github.ai.autokpass.TestData.DEFAULT_AUTOTYPE_TYPE
import com.github.ai.autokpass.TestData.KEY_PATH
import com.github.ai.autokpass.data.config.FileConfigReader.Companion.CONFIG_FILE_PATH
import com.github.ai.autokpass.data.config.FileConfigReader.Companion.ENVIRONMENT_USER_HOME
import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.arguments.Argument
import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.model.RawConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import com.github.ai.autokpass.util.StringUtils.EMPTY
import com.github.ai.autokpass.utils.mockFSProvider
import com.github.ai.autokpass.utils.mockPropertyProvider
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.every
import io.mockk.mockk
import java.io.FileNotFoundException
import org.junit.jupiter.api.Test

class FileConfigReaderTest {

    private val strings = StringResourcesImpl()

    @Test
    fun `readConfig should return config from file`() {
        // arrange
        val config = newConfig(
            filePath = DB_PATH,
            keyPath = KEY_PATH,
            startDelay = DEFAULT_DELAY.toString(),
            delayBetweenActions = DEFAULT_DELAY_BETWEEN_ACTIONS.toString(),
            autotypeType = DEFAULT_AUTOTYPE_TYPE,
            keyProcessingCommand = COMMAND
        )
        val content = config.toConfigFileContent()
        val propertyProvider = mockPropertyProvider(ENVIRONMENT_USER_HOME to HOME_PATH)
        val fsProvider = mockFSProvider(
            listOf(FULL_PATH to content.toByteArray())
        )

        // act
        val result = newReader(fsProvider, propertyProvider).readConfig()

        // assert
        result shouldBe Result.Success(config)
    }

    @Test
    fun `readConfig should return error if home is empty`() {
        // arrange
        val expectedMessage = String.format(
            strings.errorFailedToGetEnvironmentVariable,
            ENVIRONMENT_USER_HOME
        )
        val propertyProvider = mockk<SystemPropertyProvider>()

        every { propertyProvider.getSystemProperty(ENVIRONMENT_USER_HOME) }.returns(EMPTY)

        // act
        val result = newReader(mockFSProvider(), propertyProvider).readConfig()

        // assert
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `readConfig should return empty config if file doesn't exist`() {
        // arrange
        val propertyProvider = mockPropertyProvider(ENVIRONMENT_USER_HOME to HOME_PATH)
        val fsProvider = mockFSProvider(
            notExists = listOf(FULL_PATH)
        )

        // act
        val result = newReader(fsProvider, propertyProvider).readConfig()

        // assert
        result shouldBe Result.Success(RawConfig.EMPTY)
    }

    @Test
    fun `readConfig should return error if unable to read file`() {
        // arrange
        val propertyProvider = mockPropertyProvider(ENVIRONMENT_USER_HOME to HOME_PATH)
        val fsProvider = mockFSProvider(
            exists = listOf(FULL_PATH),
            errorOnRead = listOf(FULL_PATH to FileNotFoundException())
        )

        // act
        val result = newReader(fsProvider, propertyProvider).readConfig()

        // assert
        result.getExceptionOrThrow() should beInstanceOf<FileNotFoundException>()
    }

    @Test
    fun `readConfig should return empty config if file is empty`() {
        // arrange
        val propertyProvider = mockPropertyProvider(ENVIRONMENT_USER_HOME to HOME_PATH)
        val fsProvider = mockFSProvider(
            listOf(FULL_PATH to byteArrayOf())
        )

        // act
        val result = newReader(fsProvider, propertyProvider).readConfig()

        // assert
        result shouldBe Result.Success(RawConfig.EMPTY)
    }

    @Test
    fun `readConfig should return error if config is invalid`() {
        // arrange
        val expectedMessage = strings.errorFailedToParseConfigFile
        val propertyProvider = mockPropertyProvider(ENVIRONMENT_USER_HOME to HOME_PATH)
        val fsProvider = mockFSProvider(
            listOf(FULL_PATH to INVALID_CONFIG_CONTENT.toByteArray())
        )

        // act
        val result = newReader(fsProvider, propertyProvider).readConfig()

        // assert
        result.getExceptionOrThrow() should beInstanceOf<ParsingException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `readConfig should ignore commented lines`() {
        // arrange
        val config = newConfig(
            filePath = DB_PATH
        )
        val content = """
            ${Argument.FILE.fullName}=$DB_PATH
            #${Argument.KEY_FILE.fullName}=$KEY_PATH
            """.trimIndent()

        val propertyProvider = mockPropertyProvider(ENVIRONMENT_USER_HOME to HOME_PATH)
        val fsProvider = mockFSProvider(
            listOf(FULL_PATH to content.toByteArray())
        )

        // act
        val result = newReader(fsProvider, propertyProvider).readConfig()

        // assert
        result shouldBe Result.Success(config)
    }

    private fun newReader(
        fileSystemProvider: FileSystemProvider,
        systemPropertyProvider: SystemPropertyProvider
    ): FileConfigReader =
        FileConfigReader(
            fileSystemProvider = fileSystemProvider,
            systemPropertyProvider = systemPropertyProvider,
            strings = strings
        )

    private fun newConfig(
        filePath: String? = null,
        keyPath: String? = null,
        startDelay: String? = null,
        delayBetweenActions: String? = null,
        autotypeType: String? = null,
        keyProcessingCommand: String? = null
    ): RawConfig =
        RawConfig(
            filePath = filePath,
            keyPath = keyPath,
            startDelay = startDelay,
            delayBetweenActions = delayBetweenActions,
            autotypeType = autotypeType,
            keyProcessingCommand = keyProcessingCommand
        )

    private fun RawConfig.toConfigFileContent(): String {
        return StringBuilder()
            .apply {
                if (!filePath.isNullOrBlank()) {
                    append("${Argument.FILE.fullName}=$filePath\n")
                }
                if (!keyPath.isNullOrBlank()) {
                    append("${Argument.KEY_FILE.fullName}=$keyPath\n")
                }
                if (!startDelay.isNullOrBlank()) {
                    append("${Argument.DELAY.fullName}=$startDelay\n")
                }
                if (!delayBetweenActions.isNullOrBlank()) {
                    append("${Argument.AUTOTYPE_DELAY.fullName}=$delayBetweenActions\n")
                }
                if (!autotypeType.isNullOrBlank()) {
                    append("${Argument.AUTOTYPE.fullName}=$autotypeType\n")
                }
                if (!keyProcessingCommand.isNullOrBlank()) {
                    append("${Argument.PROCESS_KEY_COMMAND.fullName}=$keyProcessingCommand\n")
                }
            }
            .toString()
    }

    companion object {
        private const val HOME_PATH = "/home/user"
        private const val FULL_PATH = "$HOME_PATH/$CONFIG_FILE_PATH"
        private const val INVALID_CONFIG_CONTENT = "invalid config"
    }
}
