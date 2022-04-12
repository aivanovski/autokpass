package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeExecutorType

class AutotypeExecutorProvider(
    private val executors: Map<AutotypeExecutorType, AutotypeExecutor>
) {

    fun getExecutor(type: AutotypeExecutorType): AutotypeExecutor {
        return executors[type] ?: throw IllegalArgumentException()
    }
}