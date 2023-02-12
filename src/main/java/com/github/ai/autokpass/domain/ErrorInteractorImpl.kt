package com.github.ai.autokpass.domain

import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.util.StringUtils.EMPTY
import org.slf4j.Logger

class ErrorInteractorImpl(
    private val logger: Logger
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
            Errors.ERROR_HAS_BEEN_OCCURRED + ": " + error.exception.message
        } else {
            Errors.ERROR_HAS_BEEN_OCCURRED
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