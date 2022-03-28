package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.presentation.input.InputReader
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.model.Result

class ReadPasswordUseCase(
		private val printer: Printer,
		private val inputReader: InputReader
) {

    fun readPassword(): Result<String> {
        printer.println("Enter a password:")

        return Result.Success(inputReader.read())
    }
}