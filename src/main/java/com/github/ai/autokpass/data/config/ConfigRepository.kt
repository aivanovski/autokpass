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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ConfigRepository(
    private val fileSystemProvider: FileSystemProvider,
    private val systemPropertyProvider: SystemPropertyProvider,
    private val configParser: ConfigParser,
    private val strings: StringResources
) {

    private val current = MutableStateFlow<Result<ParsedConfig>>(
        Result.Error(EmptyConfigException(strings))
    )

    fun initialize(commandLineArguments: Array<String>): Result<ParsedConfig> {
        val configResult = CommandLineConfigReader(
            commandLineArguments,
            strings
        ).readConfig()

        if (configResult.isFailed()) {
            return configResult.asErrorOrThrow()
        }

        val fileConfigResult = readConfigFromFile()
        if (fileConfigResult.isFailed()) {
            return fileConfigResult.asErrorOrThrow()
        }

        val config = configResult.getDataOrThrow()
        val fileConfig = fileConfigResult.getDataOrThrow()

        val result = when {
            fileConfig != null && config.isEmpty() -> configParser.validateAndParse(fileConfig)
            !config.isEmpty() -> configParser.validateAndParse(config)
            else -> Result.Error(EmptyConfigException(strings))
        }

        current.value = result

        return result
    }

    fun load(): Flow<Result<ParsedConfig>> {
        return current
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

    companion object {
        private const val ENVIRONMENT_USER_HOME = "user.home"
        private const val CONFIG_FILE_PATH = ".config/autokpass/autokpass.cfg"
    }
}