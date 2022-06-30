package com.github.ai.autokpass.presentation.process

interface ProcessExecutor {

    fun execute(command: String): String

    fun executeWithBash(command: String): String

    fun execute(input: String, command: String): String

    fun execute(input: ByteArray, command: String): String
}