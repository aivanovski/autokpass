package com.github.ai.autokpass.extensions

fun Int.ensureInRange(range: IntRange): Int {
    return when {
        this < range.first -> range.first
        this > range.last -> range.last
        else -> this
    }
}