package com.github.ai.autokpass.extensions

import com.github.ai.autokpass.domain.arguments.Argument

fun Argument.getDefaultAsLong(): Long {
    return defaultValue?.toLong() ?: throw IllegalStateException()
}