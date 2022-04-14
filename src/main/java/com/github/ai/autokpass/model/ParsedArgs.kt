package com.github.ai.autokpass.model

data class ParsedArgs(
    val filePath: String,
    val keyPath: String?,
    val delayInSeconds: Long?,
    val inputReaderType: InputReaderType,
    val autotypeType: AutotypeExecutorType?,
    val keyProcessingCommand: String?,
    val isXmlKeyFile: Boolean
)