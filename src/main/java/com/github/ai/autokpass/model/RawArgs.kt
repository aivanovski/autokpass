package com.github.ai.autokpass.model

data class RawArgs(
    val password: String,
    val filePath: String,
    val selector: String,
    val autotypePattern: String
)