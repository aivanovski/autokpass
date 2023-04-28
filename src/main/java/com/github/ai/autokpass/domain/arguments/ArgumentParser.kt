package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.extensions.getDefaultAsLong
import com.github.ai.autokpass.extensions.toIntSafely
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import java.util.concurrent.TimeUnit

class ArgumentParser(
    private val fileSystemProvider: FileSystemProvider,
    private val strings: StringResources
) {

    fun validateAndParse(args: RawArgs): Result<ParsedArgs> {
        if (args.isEmpty()) {
            return Result.Error(ParsingException(strings.errorNoArgumentsWereSpecified))
        }

        val pathResult = parseFilePath(args.filePath)
        if (pathResult.isFailed()) {
            return pathResult.asErrorOrThrow()
        }

        val keyPathResult = parseKeyPath(args.keyPath)
        if (keyPathResult.isFailed()) {
            return keyPathResult.asErrorOrThrow()
        }

        val startDelayResult = parseStartDelay(args.startDelay)
        if (startDelayResult.isFailed()) {
            return startDelayResult.asErrorOrThrow()
        }

        val autotypeDelayResult = parseDelayBetweenActions(args.delayBetweenActions)
        if (autotypeDelayResult.isFailed()) {
            return autotypeDelayResult.asErrorOrThrow()
        }

        val autotypeResult = parseAutotypeExecutorType(args.autotypeType)
        if (autotypeResult.isFailed()) {
            return autotypeResult.asErrorOrThrow()
        }

        val keyProcessingCommandResult = parseKeyProcessingCommand(args.keyProcessingCommand)
        if (keyProcessingCommandResult.isFailed()) {
            return keyProcessingCommandResult.asErrorOrThrow()
        }

        return Result.Success(
            ParsedArgs(
                filePath = pathResult.getDataOrThrow(),
                keyPath = keyPathResult.getDataOrThrow(),
                startDelayInMillis = startDelayResult.getDataOrThrow(),
                delayBetweenActionsInMillis = autotypeDelayResult.getDataOrThrow(),
                autotypeType = autotypeResult.getDataOrThrow(),
                keyProcessingCommand = keyProcessingCommandResult.getDataOrThrow()
            )
        )
    }

    private fun parseFilePath(path: String?): Result<String> {
        if (path.isNullOrBlank()) {
            return Result.Error(
                ParsingException(
                    String.format(strings.errorOptionCanNotBeEmpty, Argument.FILE.cliName)
                )
            )
        }

        val isPathValidResult = isPathValid(path)
        if (isPathValidResult.isFailed()) {
            return isPathValidResult.asErrorOrThrow()
        }

        return Result.Success(path)
    }

    private fun parseKeyPath(path: String?): Result<String?> {
        if (path == null) {
            return Result.Success(null)
        }

        if (path.isBlank()) {
            return Result.Error(
                ParsingException(
                    String.format(strings.errorOptionCanNotBeEmpty, Argument.KEY_FILE.cliName)
                )
            )
        }

        val isPathValidResult = isPathValid(path)
        if (isPathValidResult.isFailed()) {
            return isPathValidResult.asErrorOrThrow()
        }

        return Result.Success(path)
    }

    private fun isPathValid(path: String): Result<Unit> {
        if (!fileSystemProvider.exists(path)) {
            return Result.Error(
                ParsingException(
                    String.format(strings.errorFileDoesNotExist, path)
                )
            )
        }

        if (!fileSystemProvider.isFile(path)) {
            return Result.Error(ParsingException(String.format(strings.errorFileIsNotFile, path)))
        }

        return Result.Success(Unit)
    }

    private fun parseStartDelay(delayStr: String?): Result<Long> {
        if (delayStr.isNullOrBlank()) {
            return Result.Success(Argument.DELAY.getDefaultAsLong())
        }

        val delay = delayStr.toIntSafely()
            ?: return Result.Error(
                ParsingException(
                    String.format(strings.errorFailedToParseArgument, Argument.DELAY.cliName, delayStr)
                )
            )

        val processedDelay = if (delay <= DELAY_MILLISECONDS_THRESHOLD) {
            TimeUnit.SECONDS.toMillis(delay.toLong())
        } else {
            delay.toLong()
        }

        return Result.Success(processedDelay)
    }

    private fun parseDelayBetweenActions(delayStr: String?): Result<Long> {
        if (delayStr.isNullOrBlank()) {
            return Result.Success(Argument.AUTOTYPE_DELAY.getDefaultAsLong())
        }

        val delay = delayStr.toIntSafely()
            ?: return Result.Error(
                ParsingException(
                    String.format(strings.errorFailedToParseArgument, Argument.AUTOTYPE_DELAY.cliName, delayStr)
                )
            )

        val processedDelay = if (delay <= DELAY_MILLISECONDS_THRESHOLD) {
            TimeUnit.SECONDS.toMillis(delay.toLong())
        } else {
            delay.toLong()
        }

        return Result.Success(processedDelay)
    }

    private fun parseAutotypeExecutorType(type: String?): Result<AutotypeExecutorType?> {
        if (type == null) {
            return Result.Success(null)
        }

        val autotypeExecutorType = AutotypeExecutorType.values()
            .firstOrNull { it.cliName.equals(type, ignoreCase = true) }
            ?: return Result.Error(
                ParsingException(
                    String.format(strings.errorFailedToParseArgument, Argument.AUTOTYPE.cliName, type)
                )
            )

        return Result.Success(autotypeExecutorType)
    }

    private fun parseKeyProcessingCommand(command: String?): Result<String?> {
        if (command == null) {
            return Result.Success(null)
        }

        if (command.isBlank()) {
            return Result.Error(
                ParsingException(
                    String.format(strings.errorOptionCanNotBeEmpty, Argument.PROCESS_KEY_COMMAND.cliName)
                )
            )
        }

        return Result.Success(command)
    }

    companion object {
        /**
         * The maximum value for delay that will be considered as seconds.
         * If the value is greater than, it will be considered as milliseconds.
         */
        private const val DELAY_MILLISECONDS_THRESHOLD = 99
    }
}