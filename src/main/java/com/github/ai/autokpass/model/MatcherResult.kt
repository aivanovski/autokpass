package com.github.ai.autokpass.model

data class MatcherResult<T>(
    val entry: T,
    val title: String,
    val highlights: List<Int>
)