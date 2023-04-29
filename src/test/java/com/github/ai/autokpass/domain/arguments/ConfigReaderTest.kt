package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.model.RawConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ConfigReaderTest {

    private val strings: StringResources = StringResourcesImpl()

    @Test
    fun `readConfig should return all nulls`() {
        // arrange
        val expected = newConfig()

        // act
        val result = CommandLineConfigReader(emptyArray(), strings).readConfig()

        // assert
        result shouldBe Result.Success(expected)
    }

    @Test
    fun `readConfig should extract all arguments by full name`() {
        // arrange
        val expected = newConfig(
            filePath = FILE_PATH,
            keyPath = KEY_PATH,
            delayInSeconds = DELAY,
            autotypeDelayInMillis = AUTOTYPE_DELAY,
            autotypeExecutorType = AUTOTYPE_EXECUTOR_TYPE,
            keyProcessingCommand = COMMAND
        )
        val args = arrayOf(
            Argument.FILE.cliName, FILE_PATH,
            Argument.KEY_FILE.cliName, KEY_PATH,
            Argument.DELAY.cliName, DELAY,
            Argument.AUTOTYPE_DELAY.cliName, AUTOTYPE_DELAY,
            Argument.AUTOTYPE.cliName, AUTOTYPE_EXECUTOR_TYPE,
            Argument.PROCESS_KEY_COMMAND.cliName, COMMAND
        )

        // act
        val result = CommandLineConfigReader(args, strings).readConfig()

        // assert
        result shouldBe Result.Success(expected)
    }

    @Test
    fun `readConfig should extract all arguments by short name`() {
        // arrange
        val expected = newConfig(
            filePath = FILE_PATH,
            keyPath = KEY_PATH,
            delayInSeconds = DELAY,
            autotypeDelayInMillis = AUTOTYPE_DELAY,
            autotypeExecutorType = AUTOTYPE_EXECUTOR_TYPE,
            keyProcessingCommand = COMMAND
        )
        val args = arrayOf(
            Argument.FILE.cliShortName, FILE_PATH,
            Argument.KEY_FILE.cliShortName, KEY_PATH,
            Argument.DELAY.cliShortName, DELAY,
            Argument.AUTOTYPE_DELAY.cliShortName, AUTOTYPE_DELAY,
            Argument.AUTOTYPE.cliShortName, AUTOTYPE_EXECUTOR_TYPE,
            Argument.PROCESS_KEY_COMMAND.cliShortName, COMMAND
        )

        // act
        val result = CommandLineConfigReader(args, strings).readConfig()

        // assert
        result shouldBe Result.Success(expected)
    }

    private fun newConfig(
        filePath: String? = null,
        keyPath: String? = null,
        delayInSeconds: String? = null,
        autotypeDelayInMillis: String? = null,
        autotypeExecutorType: String? = null,
        keyProcessingCommand: String? = null
    ): RawConfig {
        return RawConfig(
            filePath = filePath,
            keyPath = keyPath,
            startDelay = delayInSeconds,
            delayBetweenActions = autotypeDelayInMillis,
            autotypeType = autotypeExecutorType,
            keyProcessingCommand = keyProcessingCommand
        )
    }

    companion object {
        private const val FILE_PATH = "/tmp/filePath"
        private const val KEY_PATH = "/tmp/keyPath"
        private const val DELAY = "123"
        private const val COMMAND = "gpg --decrypt"
        private const val AUTOTYPE_DELAY = "456"
        private const val AUTOTYPE_EXECUTOR_TYPE = "autotypeExecutorType"
    }
}