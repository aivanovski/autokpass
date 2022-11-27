package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.util.StringUtils.EMPTY

class ErrorInteractorImpl(
    private val printer: Printer
) : ErrorInteractor {

    override fun processFailed(result: Result<*>): Boolean {
        return if (result.isFailed()) {
            process(result.asErrorOrThrow())
            true
        } else {
            false
        }
    }

    override fun process(error: Result.Error) {
        if (error.exception !is AutokpassException) {
            error.exception.printStackTrace()
        }

        printer.println(error.exception.message ?: error.exception.toString())
    }

    override fun processAndGetMessage(error: Result.Error): String {
        process(error)

        return if (!error.exception.message.isNullOrEmpty()) {
            error.exception.message ?: EMPTY
        } else {
            error.exception.toString()
        }
    }
}