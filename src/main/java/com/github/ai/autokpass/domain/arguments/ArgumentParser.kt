package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.domain.arguments.Argument.FILE
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.extensions.toIntSafely
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.InputReaderType
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.model.Result
import java.io.File

class ArgumentParser {

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
            return Result.Error(AutokpassException("Option ${FILE.cliName} can't be empty"))
        }

        val file = File(path)
        if (!file.exists()) {
            return Result.Error(AutokpassException("File doesn't exist: $path"))
        }

        if (!file.isFile) {
            return Result.Error(AutokpassException("Specified file is directory: $path"))
        }

        return Result.Success(path)
    }

    private fun parseKeyPath(path: String?): Result<String?> {
        if (path == null) {
            return Result.Success(null)
        }

        if (path.isBlank()) {
            return Result.Error(AutokpassException("Option ${Argument.KEY_FILE.cliName} can't by empty"))
        }

        val file = File(path)
        if (!file.exists()) {
            return Result.Error(AutokpassException("File doesn't exist: $path"))
        }

        if (!file.isFile) {
            return Result.Error(AutokpassException("Specified file is directory: $path"))
        }

        return Result.Success(path)
    }

    private fun parseDelay(delay: String): Result<Long?> {
        if (delay.isBlank()) {
            return Result.Success(null)
        }

        return Result.Success(delay.toIntSafely()?.toLong())
    }

    private fun parseInput(input: String): Result<InputReaderType> {
        val type = InputReaderType.values()
            .firstOrNull { it.cliName.equals(input, ignoreCase = true) }
            ?: InputReaderType.SECRET

        return Result.Success(type)
    }

    private fun parseAutotypeExecutorType(type: String): Result<AutotypeExecutorType?> {
        val autotypeExecutorType = AutotypeExecutorType.values()
            .firstOrNull { it.cliName.equals(type, ignoreCase = true) }

        if (type.isNotBlank() && autotypeExecutorType == null) {
            return Result.Error(AutokpassException("Invalid ${Argument.AUTOTYPE.cliName} option value: $type"))
        }

        return Result.Success(autotypeExecutorType)
    }
}