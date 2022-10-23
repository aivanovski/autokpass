package com.github.ai.autokpass.domain

import com.github.ai.autokpass.model.Result

interface ErrorInteractor {
    fun processFailed(result: Result<*>): Boolean
    fun process(error: Result.Error)
}