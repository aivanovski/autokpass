package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.Errors
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.exception.InvalidPasswordException
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.presentation.input.InputReader
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.model.Result

class ReadPasswordUseCase(
    private val readDatabaseUseCase: ReadDatabaseUseCase,
    private val printer: Printer,
    private val inputReader: InputReader
) {

    fun readPassword(dbFilePath: String): Result<String> {
        for (attemptIdx in 1..MAX_ATTEMPT_COUNT) {
            if (attemptIdx == 1) {
                printer.println(ENTER_PASSWORD_MESSAGE)
            }

            val password = inputReader.read()
            val readDbResult = readDatabaseUseCase.readDatabase(PasswordKey(password), dbFilePath)

            when {
                readDbResult.isFailed() && readDbResult.getErrorOrThrow().exception is InvalidPasswordException-> {
                    if (attemptIdx < MAX_ATTEMPT_COUNT) {
                        printer.println(Errors.INVALID_PASSWORD_MESSAGE)
                    }
                    continue
                }
                readDbResult.isFailed() -> {
                    return readDbResult.getErrorOrThrow()
                }
                else -> {
                    return Result.Success(password)
                }
            }
        }

        return Result.Error(AutokpassException(Errors.TOO_MANY_ATTEMPTS))
    }

    companion object {
        const val ENTER_PASSWORD_MESSAGE = "Enter a password:"
        private const val MAX_ATTEMPT_COUNT = 3
    }
}