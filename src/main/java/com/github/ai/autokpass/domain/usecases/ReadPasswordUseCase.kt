package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.exception.InvalidPasswordException
import com.github.ai.autokpass.presentation.input.InputReader
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.model.Result

class ReadPasswordUseCase(
    private val readDatabaseUseCase: ReadDatabaseUseCase,
    private val printer: Printer,
    private val inputReader: InputReader
) {

    fun readPassword(filePath: String): Result<String> {
        for (attemptIdx in 1..MAX_ATTEMPT_COUNT) {
            if (attemptIdx == 1) {
                printer.println("Enter a password:")
            }

            val password = inputReader.read()

            val readDbResult = readDatabaseUseCase.readDatabase(password, filePath)

            when {
                readDbResult.isFailed() && readDbResult.getErrorOrThrow().exception is InvalidPasswordException -> {
                    printer.println("Invalid password, please enter a password again:")
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

        return Result.Error(AutokpassException("Too many attempts"))
    }

    companion object {
        private const val MAX_ATTEMPT_COUNT = 3
    }
}