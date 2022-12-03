package com.github.ai.autokpass.model

import com.github.ai.autokpass.util.StringUtils

data class ParsedArgs(
    val filePath: String,
    val keyPath: String?,
    val delayInSeconds: Long?,
    val autotypeDelayInMillis: Long?,
    val inputReaderType: InputReaderType,
    val autotypeType: AutotypeExecutorType?,
    val keyProcessingCommand: String?
) {

    companion object {
        val EMPTY = ParsedArgs(
            filePath = StringUtils.EMPTY,
            keyPath = null,
            delayInSeconds = null,
            autotypeDelayInMillis = null,
            inputReaderType = InputReaderType.STANDARD,
            autotypeType = null,
            keyProcessingCommand = null
        )
    }
}