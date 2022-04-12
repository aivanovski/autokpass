package com.github.ai.autokpass.model

data class RawArgs(
    val filePath: String,
    val keyPath: String?,
    val delayInSeconds: String,
    val inputType: String,
    val autotypeType: String,
    val isXmlKeyFile: Boolean
)