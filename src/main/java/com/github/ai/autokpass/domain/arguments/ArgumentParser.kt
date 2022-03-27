package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.domain.arguments.Argument.LAUNCH_MODE
import com.github.ai.autokpass.domain.autotype.AutotypePatternParser
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.extensions.toIntSafely
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.LaunchMode
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.model.Result
import java.io.File
import java.util.UUID

class ArgumentParser(
    private val patternParser: AutotypePatternParser
) {

    fun validateAndParse(args: RawArgs): Result<ParsedArgs> {
        val launchModeResult = parseLaunchMode(args.launchMode)
        if (launchModeResult.isFailed()) {
            return launchModeResult.getErrorOrThrow()
        }

        val launchMode = launchModeResult.getDataOrThrow()
        val passwordResult = validatePassword(args.password, launchMode)
        if (passwordResult.isFailed()) {
            return passwordResult.getErrorOrThrow()
        }

        val pathResult = validateFilePath(args.filePath, launchMode)
        if (pathResult.isFailed()) {
            return pathResult.getErrorOrThrow()
        }

        val patternResult = parseAutotypePattern(args.pattern)
        if (patternResult.isFailed()) {
            return patternResult.getErrorOrThrow()
        }

        val uidResult = parseUid(args.uid)
        if (uidResult.isFailed()) {
            return uidResult.getErrorOrThrow()
        }

        val delayResult = parseDelay(args.delayInSeconds)
        if (delayResult.isFailed()) {
            return delayResult.getErrorOrThrow()
        }

        return Result.Success(
            ParsedArgs(
                args.password,
                args.filePath,
                patternResult.getDataOrThrow(),
                uidResult.getDataOrThrow(),
                delayResult.getDataOrThrow(),
                launchMode
            )
        )
    }

    private fun validatePassword(password: String, launchMode: LaunchMode): Result<Unit> {
        if (launchMode != LaunchMode.PRINT_ALL) {
            return Result.Success(Unit)
        }

        if (password.isEmpty()) {
            return Result.Error(AutokpassException("Password is empty"))
        }

        return Result.Success(Unit)
    }

    private fun validateFilePath(path: String, launchMode: LaunchMode): Result<Unit> {
        if (launchMode != LaunchMode.PRINT_ALL) {
            return Result.Success(Unit)
        }

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

    private fun parseAutotypePattern(pattern: String): Result<AutotypePattern?> {
        return Result.Success(patternParser.parse(pattern))
    }

    private fun parseLaunchMode(launchMode: String): Result<LaunchMode> {
        val parsedLaunchMode = launchModeNameToTypeMap[launchMode.lowercase().trim()]
            ?: return Result.Error(AutokpassException("Unable to parse ${LAUNCH_MODE.cliName} value"))

        return Result.Success(parsedLaunchMode)
    }

    private fun parseUid(uid: String): Result<UUID?> {
        if (uid.isBlank()) {
            return Result.Success(null)
        }

        val parsedUid = try {
            UUID.fromString(uid)
        } catch (e: Exception) {
            null
        }

        return Result.Success(parsedUid)
    }

    private fun parseDelay(delay: String): Result<Long?> {
        if (delay.isBlank()) {
            return Result.Success(null)
        }

        return Result.Success(delay.toIntSafely()?.toLong())
    }

    companion object {
        private val launchModeNameToTypeMap = mapOf(
            Pair(LaunchMode.PRINT_ALL.cliName, LaunchMode.PRINT_ALL),
            Pair(LaunchMode.AUTOTYPE.cliName, LaunchMode.AUTOTYPE),
        )
    }
}