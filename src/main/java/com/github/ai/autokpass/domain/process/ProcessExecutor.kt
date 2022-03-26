package com.github.ai.autokpass.domain.process

interface ProcessExecutor {

    fun execute(command: String): String

    fun execute(input: String, command: String): String
}