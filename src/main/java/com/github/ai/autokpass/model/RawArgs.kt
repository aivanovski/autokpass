package com.github.ai.autokpass.model

data class RawArgs(
    val filePath: String,
    val keyPath: String?,
    val delayInSeconds: String?,
    val autotypeDelayInMillis: String?,
    val inputType: String?,
    val autotypeType: String?,
    val keyProcessingCommand: String?,
    val isXmlKeyFile: Boolean
)