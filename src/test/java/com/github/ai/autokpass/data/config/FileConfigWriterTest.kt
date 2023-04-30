package com.github.ai.autokpass.data.config

import com.github.ai.autokpass.TestData.COMMAND
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.DEFAULT_DELAY
import com.github.ai.autokpass.TestData.DEFAULT_DELAY_BETWEEN_ACTIONS
import com.github.ai.autokpass.TestData.KEY_PATH
import com.github.ai.autokpass.data.config.FileConfigReader.Companion.CONFIG_FILE_PATH
import com.github.ai.autokpass.data.config.FileConfigReader.Companion.ENVIRONMENT_USER_HOME
import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.arguments.Argument
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.extensions.getDefaultAsLong
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import com.github.ai.autokpass.util.StringUtils.EMPTY
import com.github.ai.autokpass.utils.WritableFileSystemProvider
import com.github.ai.autokpass.utils.mockFSProvider
import com.github.ai.autokpass.utils.mockPropertyProvider
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import java.io.IOException
import org.junit.jupiter.api.Test

class FileConfigWriterTest {

    private val strings = StringResourcesImpl()

    @Test
    fun `writeConfig should write config to file`() {
        // arrange
        val config = newConfig(
            filePath = DB_PATH,
            keyPath = KEY_PATH,
            startDelayInMillis = DEFAULT_DELAY,
            delayBetweenActionsInMillis = DEFAULT_DELAY_BETWEEN_ACTIONS,
            autotypeType = AutotypeExecutorType.XDOTOOL,
            keyProcessingCommand = COMMAND
        )
        val content = FileMarshaller(strings).marshall(config.toMap())
        val fsProvider = WritableFileSystemProvider()
        val propertyProvider = mockPropertyProvider(ENVIRONMENT_USER_HOME to HOME_PATH)

        // act
        val result = newWriter(fsProvider, propertyProvider).writeConfig(config)

        // assert
        fsProvider.getWrittenFile(FULL_PATH) shouldBe content
        result shouldBe Result.Success(Unit)
    }

    @Test
    fun `writeConfig should return error if home is empty`() {
        // arrange
        val expectedMessage = String.format(
            strings.errorFailedToGetEnvironmentVariable,
            ENVIRONMENT_USER_HOME
        )
        val propertyProvider = mockPropertyProvider(ENVIRONMENT_USER_HOME to EMPTY)

        // act
        val result = newWriter(mockFSProvider(), propertyProvider).writeConfig(newConfig())

        // assert
        result.getExceptionOrThrow() should beInstanceOf<AutokpassException>()
        result.getExceptionOrThrow().message shouldBe expectedMessage
    }

    @Test
    fun `writeConfig should not write default values to file`() {
        // arrange
        val config = newConfig(
            startDelayInMillis = Argument.DELAY.getDefaultAsLong(),
            delayBetweenActionsInMillis = Argument.AUTOTYPE_DELAY.getDefaultAsLong()
        )
        val content = FileMarshaller(strings).marshall(
            mapOf(Argument.FILE.fullName to config.filePath)
        )
        val fsProvider = WritableFileSystemProvider()
        val propertyProvider = mockPropertyProvider(ENVIRONMENT_USER_HOME to HOME_PATH)

        // act
        val result = newWriter(fsProvider, propertyProvider).writeConfig(config)

        // assert
        fsProvider.getWrittenFile(FULL_PATH) shouldBe content
        result shouldBe Result.Success(Unit)
    }

    @Test
    fun `writeConfig should return error if write failed`() {
        // arrange
        val fsProvider = mockFSProvider(
            errorsOnWrite = listOf(FULL_PATH to IOException())
        )
        val propertyProvider = mockPropertyProvider(ENVIRONMENT_USER_HOME to HOME_PATH)

        // act
        val result = newWriter(fsProvider, propertyProvider).writeConfig(newConfig())

        // assert
        result.getExceptionOrThrow() should beInstanceOf<IOException>()
    }

    private fun newWriter(
        fileSystemProvider: FileSystemProvider,
        systemPropertyProvider: SystemPropertyProvider
    ): FileConfigWriter =
        FileConfigWriter(
            fileSystemProvider = fileSystemProvider,
            systemPropertyProvider = systemPropertyProvider,
            strings = strings
        )

    private fun newConfig(
        filePath: String = DB_PATH,
        keyPath: String? = null,
        startDelayInMillis: Long = Argument.DELAY.getDefaultAsLong(),
        delayBetweenActionsInMillis: Long = Argument.AUTOTYPE_DELAY.getDefaultAsLong(),
        autotypeType: AutotypeExecutorType? = null,
        keyProcessingCommand: String? = null
    ): ParsedConfig =
        ParsedConfig(
            filePath = filePath,
            keyPath = keyPath,
            startDelayInMillis = startDelayInMillis,
            delayBetweenActionsInMillis = delayBetweenActionsInMillis,
            autotypeType = autotypeType,
            keyProcessingCommand = keyProcessingCommand
        )

    private fun ParsedConfig.toMap(): Map<String, String> {
        return LinkedHashMap<String, String>()
            .apply {
                this[Argument.FILE.fullName] = filePath
                if (keyPath != null) {
                    this[Argument.KEY_FILE.fullName] = keyPath ?: EMPTY
                }
                if (startDelayInMillis != Argument.DELAY.getDefaultAsLong()) {
                    this[Argument.DELAY.fullName] = startDelayInMillis.toString()
                }
                if (delayBetweenActionsInMillis != Argument.AUTOTYPE_DELAY.getDefaultAsLong()) {
                    this[Argument.AUTOTYPE_DELAY.fullName] = delayBetweenActionsInMillis.toString()
                }
                if (autotypeType != null) {
                    this[Argument.AUTOTYPE.fullName] = autotypeType?.cliName ?: EMPTY
                }
                if (keyProcessingCommand != null) {
                    this[Argument.PROCESS_KEY_COMMAND.fullName] = keyProcessingCommand ?: EMPTY
                }
            }
    }

    companion object {
        private const val HOME_PATH = "/home/user"
        private const val FULL_PATH = "$HOME_PATH/$CONFIG_FILE_PATH"
    }
}