package com.github.ai.autokpass.model

data class ParsedArgs(
    val filePath: String,
    val delayInSeconds: Long?,
    val inputReaderType: InputReaderType
)