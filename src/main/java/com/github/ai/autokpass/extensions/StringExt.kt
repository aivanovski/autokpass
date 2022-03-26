package com.github.ai.autokpass.extensions

fun String.splitIntoCommandAndArgs(): Pair<String, List<String>> {
    if (!this.contains(" ")) {
        return Pair(this, emptyList())
    }

    val values = this.split(" ")

    return Pair(values[0], values.subList(1, values.size))
}

fun String.toIntSafely(): Int? {
    val isDigitsOnly = this.toCharArray()
        .mapIndexed { index, char ->
            char.isDigit() || (index == 0 && char == '-')
        }
        .all { it }

    if (!isDigitsOnly) {
        return null
    }

    return try {
        Integer.parseInt(this)
    } catch (e: Exception) {
        null
    }
}