package com.github.ai.autokpass.data.config

import com.github.ai.autokpass.data.config.FileConfigReader.Companion.CONFIG_FILE_PATH
import com.github.ai.autokpass.data.config.FileConfigReader.Companion.ENVIRONMENT_USER_HOME
import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.arguments.Argument
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.extensions.toLinkedHashMap
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.util.StringUtils.EMPTY

class FileConfigWriter(
    private val fileSystemProvider: FileSystemProvider,
    private val systemPropertyProvider: SystemPropertyProvider,
    private val strings: StringResources
) {

    fun writeConfig(config: ParsedConfig): Result<Unit> {
        val pathResult = getOutputPath()
        if (pathResult.isFailed()) {
            return pathResult.asErrorOrThrow()
        }

        val path = pathResult.getDataOrThrow()
        val bytes = FileMarshaller(strings)
            .marshall(config.toMap())
            .toByteArray()

        return fileSystemProvider.writeFile(path, bytes)
    }

    private fun getOutputPath(): Result<String> {
        val homePath = systemPropertyProvider.getSystemProperty(ENVIRONMENT_USER_HOME)
        if (homePath.isEmpty()) {
            return Result.Error(
                AutokpassException(
                    String.format(
                        strings.errorFailedToGetEnvironmentVariable,
                        ENVIRONMENT_USER_HOME
                    )
                )
            )
        }

        return Result.Success("$homePath/$CONFIG_FILE_PATH")
    }

    private fun ParsedConfig.toMap(): Map<String, String> {
        return listOf(
            Argument.FILE to filePath,
            Argument.KEY_FILE to (keyPath ?: EMPTY),
            Argument.DELAY to startDelayInMillis.toString(),
            Argument.AUTOTYPE_DELAY to delayBetweenActionsInMillis.toString(),
            Argument.AUTOTYPE to (autotypeType?.cliName ?: EMPTY),
            Argument.PROCESS_KEY_COMMAND to (keyProcessingCommand ?: EMPTY)
        )
            .filter { (argument, value) ->
                value.isNotEmpty() && value != argument.defaultValue
            }
            .map { (argument, value) ->
                argument.fullName to value
            }
            .toLinkedHashMap()
    }
}