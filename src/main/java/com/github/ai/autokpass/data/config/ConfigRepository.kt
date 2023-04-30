package com.github.ai.autokpass.data.config

import com.github.ai.autokpass.domain.exception.EmptyConfigException
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ConfigRepository(
    private val reader: FileConfigReader,
    private val writer: FileConfigWriter,
    private val parsed: ConfigParser,
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

        val fileConfigResult = reader.readConfig()
        if (fileConfigResult.isFailed()) {
            return fileConfigResult.asErrorOrThrow()
        }

        val config = configResult.getDataOrThrow()
        val fileConfig = fileConfigResult.getDataOrThrow()

        val result = when {
            !fileConfig.isEmpty() && config.isEmpty() -> parsed.validateAndParse(fileConfig)
            !config.isEmpty() -> parsed.validateAndParse(config)
            else -> Result.Error(EmptyConfigException(strings))
        }

        current.value = result

        return result
    }

    fun load(): Flow<Result<ParsedConfig>> {
        return current
    }

    fun getCurrent(): Result<ParsedConfig> {
        return current.value
    }

    fun save(config: ParsedConfig): Result<Unit> {
        val result = writer.writeConfig(config)

        if (result.isSucceeded()) {
            current.value = Result.Success(config)
        }

        return result
    }
}