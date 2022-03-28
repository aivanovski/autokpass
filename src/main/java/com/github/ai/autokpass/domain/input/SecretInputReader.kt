package com.github.ai.autokpass.domain.input

class SecretInputReader : InputReader {

    override fun read(): String {
        val chars = System.console().readPassword()
        return chars?.let { String(it) } ?: ""
    }
}