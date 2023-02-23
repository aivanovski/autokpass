package com.github.ai.autokpass.domain

import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.util.StringUtils.EMPTY
import org.slf4j.Logger

class ErrorInteractorImpl(
    private val logger: Logger,
    private val strings: StringResources
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
        val message = if (!error.exception.message.isNullOrEmpty()) {
            strings.errorHasBeenOccurred + ": " + error.exception.message
        } else {
            strings.errorHasBeenOccurred
        }

        logger.error(message, error.exception)
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