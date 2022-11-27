package com.github.ai.autokpass.domain

import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.util.StringUtils.EMPTY

class ErrorInteractorTestImpl : ErrorInteractor {

    override fun processFailed(result: Result<*>): Boolean {
        return result.isFailed()
    }

    override fun process(error: Result.Error) {
    }

    override fun processAndGetMessage(error: Result.Error): String {
        return error.exception.message ?: EMPTY
    }
}