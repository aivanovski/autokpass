package com.github.ai.autokpass.presentation.process

interface ProcessExecutor {

    fun execute(command: String): String

    fun execute(input: String, command: String): String
}