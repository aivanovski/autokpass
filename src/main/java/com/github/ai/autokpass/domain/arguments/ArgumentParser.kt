package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.Errors.GENERIC_EMPTY_ARGUMENT
import com.github.ai.autokpass.domain.Errors.GENERIC_FAILED_TO_PARSE_ARGUMENT
import com.github.ai.autokpass.domain.Errors.GENERIC_FILE_DOES_NOT_EXIST
import com.github.ai.autokpass.domain.Errors.GENERIC_FILE_IS_NOT_A_FILE
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.extensions.toIntSafely
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.InputReaderType
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.model.Result

class ArgumentParser(
    private val fileSystemProvider: FileSystemProvider
) {

    fun validateAndParse(args: RawArgs): Result<ParsedArgs> {
        val pathResult = parseFilePath(args.filePath)
        if (pathResult.isFailed()) {
            return pathResult.getErrorOrThrow()
        }

        val keyPathResult = parseKeyPath(args.keyPath)
        if (keyPathResult.isFailed()) {
            return keyPathResult.getErrorOrThrow()
        }

        val delayResult = parseDelay(args.delayInSeconds)
        if (delayResult.isFailed()) {
            return delayResult.getErrorOrThrow()
        }

        val inputTypeResult = parseInput(args.inputType)
        if (inputTypeResult.isFailed()) {
            return inputTypeResult.getErrorOrThrow()
        }

        val autotypeResult = parseAutotypeExecutorType(args.autotypeType)
        if (autotypeResult.isFailed()) {
            return autotypeResult.getErrorOrThrow()
        }

        return Result.Success(
            ParsedArgs(
                pathResult.getDataOrThrow(),
                keyPathResult.getDataOrThrow(),
                delayResult.getDataOrThrow(),
                inputTypeResult.getDataOrThrow(),
                autotypeResult.getDataOrThrow(),
                args.isXmlKeyFile
            )
        )
    }

    private fun parseFilePath(path: String): Result<String> {
        if (path.isBlank()) {
            return Result.Error(ParsingException(String.format(GENERIC_EMPTY_ARGUMENT, Argument.FILE.cliName)))
        }

        val isPathValidResult = isPathValid(path)
        if (isPathValidResult.isFailed()) {
            return isPathValidResult.getErrorOrThrow()
        }

        return Result.Success(path)
    }

    private fun parseKeyPath(path: String?): Result<String?> {
        if (path == null) {
            return Result.Success(null)
        }

        if (path.isBlank()) {
            return Result.Error(ParsingException(String.format(GENERIC_EMPTY_ARGUMENT, Argument.KEY_FILE.cliName)))
        }

        val isPathValidResult = isPathValid(path)
        if (isPathValidResult.isFailed()) {
            return isPathValidResult.getErrorOrThrow()
        }

        return Result.Success(path)
    }

    private fun isPathValid(path: String): Result<Unit> {
        if (!fileSystemProvider.exists(path)) {
            return Result.Error(ParsingException(String.format(GENERIC_FILE_DOES_NOT_EXIST, path)))
        }

        if (!fileSystemProvider.isFile(path)) {
            return Result.Error(ParsingException(String.format(GENERIC_FILE_IS_NOT_A_FILE, path)))
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
                    String.format(GENERIC_FAILED_TO_PARSE_ARGUMENT, Argument.DELAY.cliName, delayStr)
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
                    String.format(GENERIC_FAILED_TO_PARSE_ARGUMENT, Argument.INPUT.cliName, input)
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
                AutokpassException(
                    String.format(GENERIC_FAILED_TO_PARSE_ARGUMENT, Argument.AUTOTYPE.cliName, type)
                )
            )

        return Result.Success(autotypeExecutorType)
    }
}