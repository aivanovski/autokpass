package com.github.ai.autokpass.presentation.process

import com.github.ai.autokpass.model.Result

interface ProcessExecutor {

    fun execute(command: String): Result<String>
    fun executeWithBash(command: String): Result<String>
    fun execute(input: ByteArray, command: String): Result<String>
}