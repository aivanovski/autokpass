package com.github.ai.autokpass.data.config

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.arguments.Argument
import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.model.RawConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class FileConfigReader(
    private val fileSystemProvider: FileSystemProvider,
    private val systemPropertyProvider: SystemPropertyProvider,
    private val strings: StringResources
) : ConfigReader {

    override fun readConfig(): Result<RawConfig> {
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
            return Result.Success(RawConfig.EMPTY)
        }

        val readFileResult = fileSystemProvider.readFile(configPath)
        if (readFileResult.isFailed()) {
            return readFileResult.asErrorOrThrow()
        }

        val bytes = readFileResult.getDataOrThrow()
        val configResult = readContent(ByteArrayInputStream(bytes))
        if (configResult.isFailed()) {
            return configResult.asErrorOrThrow()
        }

        val config = configResult.getDataOrThrow()
        return if (!config.isEmpty()) {
            Result.Success(config)
        } else {
            Result.Success(RawConfig.EMPTY)
        }
    }

    private fun readContent(content: InputStream): Result<RawConfig> {
        return try {
            val reader = InputStreamReader(content)

            val valuesResult = FileMarshaller(strings).unmarshall(reader.readText())
            if (valuesResult.isFailed()) {
                return valuesResult.asErrorOrThrow()
            }

            val values = valuesResult.getDataOrThrow()
            val config = RawConfig(
                filePath = values[Argument.FILE.fullName],
                keyPath = values[Argument.KEY_FILE.fullName],
                startDelay = values[Argument.DELAY.fullName],
                delayBetweenActions = values[Argument.AUTOTYPE_DELAY.fullName],
                autotypeType = values[Argument.AUTOTYPE.fullName],
                keyProcessingCommand = values[Argument.PROCESS_KEY_COMMAND.fullName]
            )

            Result.Success(config)
        } catch (exception: IOException) {
            Result.Error(exception)
        }
    }

    companion object {
        internal const val ENVIRONMENT_USER_HOME = "user.home"
        internal const val CONFIG_FILE_PATH = ".config/autokpass/autokpass.cfg"
    }
}