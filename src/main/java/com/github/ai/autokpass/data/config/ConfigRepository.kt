package com.github.ai.autokpass.data.config

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.arguments.CommandLineConfigReader
import com.github.ai.autokpass.domain.arguments.ConfigParser
import com.github.ai.autokpass.domain.arguments.FileConfigReader
import com.github.ai.autokpass.domain.exception.EmptyConfigException
import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.RawConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import java.io.ByteArrayInputStream
import kotlinx.coroutines.flow.MutableStateFlow

class ConfigRepository(
    private val fileSystemProvider: FileSystemProvider,
    private val systemPropertyProvider: SystemPropertyProvider,
    private val configParser: ConfigParser,
    private val strings: StringResources
) {

    private val currentConfig = MutableStateFlow<ConfigState>(ConfigState.Empty)

    fun initialize(commandLineArguments: Array<String>): Result<ParsedConfig> {
        val readCommandLineValuesResult = CommandLineConfigReader(
            commandLineArguments,
            strings
        ).readConfig()

        if (readCommandLineValuesResult.isFailed()) {
            return readCommandLineValuesResult.asErrorOrThrow()
        }

        val readFileValuesResult = readConfigFromFile()
        if (readFileValuesResult.isFailed()) {
            return readFileValuesResult.asErrorOrThrow()
        }

        val commandLineValues = readCommandLineValuesResult.getDataOrThrow()
        val fileValues = readFileValuesResult.getDataOrThrow()

        val config = when {
            fileValues != null && commandLineValues.isEmpty() -> {
                ConfigState.FileConfig(
                    config = configParser.validateAndParse(fileValues)
                )
            }

            !commandLineValues.isEmpty() -> {
                ConfigState.CommandLineConfig(
                    config = configParser.validateAndParse(commandLineValues)
                )
            }

            else -> ConfigState.Empty
        }

        currentConfig.value = config

        return when (config) {
            is ConfigState.CommandLineConfig -> config.config
            is ConfigState.FileConfig -> config.config
            else -> Result.Error(EmptyConfigException(strings))
        }
    }

    fun getCurrent(): Result<ParsedConfig> {
        return when (val config = currentConfig.value) {
            is ConfigState.CommandLineConfig -> config.config
            is ConfigState.FileConfig -> config.config
            else -> Result.Error(EmptyConfigException(strings))
        }
    }

    private fun readConfigFromFile(): Result<RawConfig?> {
        val homePath = systemPropertyProvider.getSystemProperty(ENVIRONMENT_USER_HOME)
        if (homePath.isEmpty()) {
            return Result.Error(
                ParsingException(
                    String.format(
                        strings.errorFailedToGetEnvironmentVariable,
                        ENVIRONMENT_USER_HOME
                    )
                )
            )
        }

        val configPath = "$homePath/$CONFIG_FILE_PATH"
        if (!fileSystemProvider.exists(configPath)) {
            return Result.Success(null)
        }

        val readFileResult = fileSystemProvider.readFile(configPath)
        if (readFileResult.isFailed()) {
            return readFileResult.asErrorOrThrow()
        }

        val bytes = readFileResult.getDataOrThrow()
        if (String(bytes).trim().isEmpty()) {
            return Result.Success(null)
        }

        val content = ByteArrayInputStream(bytes)
        val configResult = FileConfigReader(strings, content).readConfig()
        if (configResult.isFailed()) {
            return configResult.asErrorOrThrow()
        }

        val config = configResult.getDataOrThrow()
        return if (!config.isEmpty()) {
            Result.Success(config)
        } else {
            Result.Success(null)
        }
    }

    private sealed class ConfigState {

        object Empty : ConfigState()

        data class CommandLineConfig(
            val config: Result<ParsedConfig>
        ) : ConfigState()

        data class FileConfig(
            val config: Result<ParsedConfig>
        ) : ConfigState()
    }

    companion object {
        private const val ENVIRONMENT_USER_HOME = "user.home"
        private const val CONFIG_FILE_PATH = ".config/autokpass/autokpass.cfg"
    }
}