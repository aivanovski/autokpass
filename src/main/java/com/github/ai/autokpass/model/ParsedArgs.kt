package com.github.ai.autokpass.model

import java.util.UUID

data class ParsedArgs(
    val password: String,
    val filePath: String,
    val pattern: AutotypePattern?,
    val uid: UUID?,
    val delayInSeconds: Long?,
    val launchMode: LaunchMode
)