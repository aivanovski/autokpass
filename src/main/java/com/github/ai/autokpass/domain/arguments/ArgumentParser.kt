package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.extensions.toIntSafely
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.InputReaderType
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources

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

        val delayResult = parseDelay(args.delayInSeconds)
        if (delayResult.isFailed()) {
            return delayResult.asErrorOrThrow()
        }

        val autotypeDelayResult = parseAutotypeDelay(args.autotypeDelayInMillis)
        if (autotypeDelayResult.isFailed()) {
            return autotypeDelayResult.asErrorOrThrow()
        }

        val inputTypeResult = parseInput(args.inputType)
        if (inputTypeResult.isFailed()) {
            return inputTypeResult.asErrorOrThrow()
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
                delayInSeconds = delayResult.getDataOrThrow(),
                autotypeDelayInMillis = autotypeDelayResult.getDataOrThrow(),
                inputReaderType = inputTypeResult.getDataOrThrow(),
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

    private fun parseDelay(delayStr: String?): Result<Long?> {
        if (delayStr.isNullOrBlank()) {
            return Result.Success(null)
        }

        val delay = delayStr.toIntSafely()
            ?: return Result.Error(
                ParsingException(
                    String.format(strings.errorFailedToParseArgument, Argument.DELAY.cliName, delayStr)
                )
            )

        return Result.Success(delay.toLong())
    }

    private fun parseAutotypeDelay(delayStr: String?): Result<Long?> {
        if (delayStr.isNullOrBlank()) {
            return Result.Success(null)
        }

        val delay = delayStr.toIntSafely()
            ?: return Result.Error(
                ParsingException(
                    String.format(strings.errorFailedToParseArgument, Argument.AUTOTYPE_DELAY.cliName, delayStr)
                )
            )

        return Result.Success(delay.toLong())
    }

    private fun parseInput(input: String?): Result<InputReaderType> {
        if (input == null) {
            return Result.Success(InputReaderType.SECRET)
        }

        val type = InputReaderType.values()
            .firstOrNull { it.cliName.equals(input, ignoreCase = true) }
            ?: return Result.Error(
                ParsingException(
                    String.format(strings.errorFailedToParseArgument, Argument.INPUT.cliName, input)
                )
            )

        return Result.Success(type)
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
}