package com.github.ai.autokpass.model

data class ParsedArgs(
    val password: String,
    val filePath: String,
    val selector: SelectorType,
    val patterns: List<AutotypePattern>
)