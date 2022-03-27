package com.github.ai.autokpass.model

data class RawArgs(
    val password: String,
    val filePath: String,
    val pattern: String,
    val uid: String,
    val delayInSeconds: String,
    val launchMode: String
)