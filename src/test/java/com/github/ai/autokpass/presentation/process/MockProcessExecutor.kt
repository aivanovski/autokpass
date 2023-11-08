package com.github.ai.autokpass.presentation.process

import com.github.ai.autokpass.model.Result

class MockProcessExecutor(
    private val data: Map<String, Result<String>>
) : ProcessExecutor {

    override fun execute(command: String): Result<String> {
        return data[command]
            ?: throw IllegalArgumentException("Unable to find data for command: $command")
    }

    override fun executeWithBash(command: String): Result<String> {
        throw NotImplementedError()
    }

    override fun execute(input: ByteArray, command: String): Result<String> {
        throw NotImplementedError()
    }
}