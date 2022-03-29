package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.domain.autotype.AutotypeSequenceParser
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.extensions.toIntSafely
import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.InputReaderType
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.util.StringUtils.EMPTY
import java.io.File
import java.util.Base64

class ArgumentParser(
    private val autotypeSequenceParser: AutotypeSequenceParser
) {

    fun validateAndParse(args: RawArgs): Result<ParsedArgs> {
        val sequenceResult = parseAutotypeSequence(args.autotypeSequence)
        if (sequenceResult.isFailed()) {
            return sequenceResult.getErrorOrThrow()
        }

        val isSequenceSpecified = (sequenceResult.getDataOrThrow() != null)

        val pathResult = parseFilePath(args.filePath, isSequenceSpecified)
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
                inputTypeResult.getDataOrThrow(),
                sequenceResult.getDataOrThrow(),
                args.isSingleProcess
            )
        )
    }

    // TODO: should return String?
    private fun parseFilePath(path: String, isSequenceSpecified: Boolean): Result<String> {
        if (path.isBlank() && isSequenceSpecified) {
            return Result.Success(EMPTY)
        }

        if (path.isBlank()) {
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

    private fun parseAutotypeSequence(sequence: String): Result<AutotypeSequence?> {
        val decodedBytes = Base64.getDecoder().decode(sequence)
            ?: return Result.Error(
                AutokpassException("Failed to parse ${Argument.AUTOTYPE_SEQUENCE.cliName} argument value")
            )

        return Result.Success(autotypeSequenceParser.parse(String(decodedBytes)))
    }
}