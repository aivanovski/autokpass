package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.model.SelectorType
import java.io.File

class ArgumentParser {

    fun validateAndParse(args: RawArgs): Result<ParsedArgs> {
        val passwordResult = validatePassword(args.password)
        if (passwordResult.isFailed()) {
            return passwordResult.getErrorOrThrow()
        }

        val pathResult = validateFilePath(args.filePath)
        if (pathResult.isFailed()) {
            return pathResult.getErrorOrThrow()
        }

        val parseSelectorResult = parseSelector(args.selector)
        if (parseSelectorResult.isFailed()) {
            return parseSelectorResult.getErrorOrThrow()
        }

        val parsePatternResult = parseAutotypePattern(args.autotypePattern)
        if (parsePatternResult.isFailed()) {
            return parsePatternResult.getErrorOrThrow()
        }

        return Result.Success(
            ParsedArgs(
                args.password,
                args.filePath,
                parseSelectorResult.getDataOrThrow(),
                parsePatternResult.getDataOrThrow()
            )
        )
    }

    private fun validatePassword(password: String): Result<Unit> {
        if (password.isEmpty()) {
            return Result.Error(AutokpassException("Password is empty"))
        }

        return Result.Success(Unit)
    }

    private fun validateFilePath(path: String): Result<Unit> {
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

        return Result.Success(Unit)
    }

    private fun parseSelector(selector: String): Result<SelectorType> {
        val selectorType = selectorNameToTypeMap[selector.lowercase().trim()]
        return Result.Success(selectorType ?: SelectorType.STANDARD_OUTPUT)
    }

    private fun parseAutotypePattern(pattern: String): Result<List<AutotypePattern>> {
        // TODO: implement
        return Result.Success(listOf(AutotypePattern.DEFAULT_PATTERN))
    }

    companion object {
        private val selectorNameToTypeMap = mapOf(
            Pair("stdout", SelectorType.STANDARD_OUTPUT),
            Pair("fzf", SelectorType.FZF)
        )
    }
}