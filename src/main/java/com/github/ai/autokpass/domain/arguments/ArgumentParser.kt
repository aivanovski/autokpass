package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.extensions.toIntSafely
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

        val delayResult = parseDelay(args.delayInSeconds)
        if (delayResult.isFailed()) {
            return delayResult.getErrorOrThrow()
        }

        val inputTypeResult = parseInput(args.inputType)
        if (inputTypeResult.isFailed()) {
            return inputTypeResult.getErrorOrThrow()
        }

        return Result.Success(
            ParsedArgs(
                pathResult.getDataOrThrow(),
                delayResult.getDataOrThrow(),
                inputTypeResult.getDataOrThrow()
            )
        )
    }

    private fun parseFilePath(path: String): Result<String> {
        if (path.isEmpty()) {
            return Result.Error(AutokpassException("Path is empty"))
        }

        val file = File(path)
        if (!file.exists()) {
            return Result.Error(AutokpassException("File does not exist"))
        }

        if (!file.isFile) {
            return Result.Error(AutokpassException("Invalid file type"))
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
}