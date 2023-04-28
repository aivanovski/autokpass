package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.arguments.FileArgumentExtractor
import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import java.io.ByteArrayInputStream

class ReadConfigFileUseCase(
    private val systemPropertyProvider: SystemPropertyProvider,
    private val fileSystemProvider: FileSystemProvider,
    private val strings: StringResources
) {

    fun readConfigArgs(): Result<RawArgs?> {
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
        val argsResult = FileArgumentExtractor(strings, content).extractArguments()
        if (argsResult.isFailed()) {
            return argsResult.asErrorOrThrow()
        }

        val args = argsResult.getDataOrThrow()
        return if (!args.isEmpty()) {
            Result.Success(args)
        } else {
            Result.Success(null)
        }
    }

    companion object {
        private const val ENVIRONMENT_USER_HOME = "user.home"
        private const val CONFIG_FILE_PATH = ".config/autokpass/autokpass.cfg"
    }
}