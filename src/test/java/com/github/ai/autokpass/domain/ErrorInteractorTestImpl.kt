package com.github.ai.autokpass.domain

import com.github.ai.autokpass.model.Result

class ErrorInteractorTestImpl : ErrorInteractor {

    override fun processFailed(result: Result<*>): Boolean {
        return result.isFailed()
    }

    override fun process(error: Result.Error) {
    }
}