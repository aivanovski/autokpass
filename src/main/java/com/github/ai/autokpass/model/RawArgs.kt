package com.github.ai.autokpass.model

data class RawArgs(
    val filePath: String,
    val delayInSeconds: String,
    val inputType: String,
    val autotypeSequence: String,
    val isSingleProcess: Boolean
)