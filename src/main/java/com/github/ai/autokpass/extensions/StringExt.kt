package com.github.ai.autokpass.extensions

fun String.splitIntoCommandAndArgs(): Pair<String, List<String>> {
    if (this.isBlank()) {
        return Pair(this, emptyList())
    }

    if (!this.contains(" ")) {
        return Pair(this, emptyList())
    }

    val values = this.split(" ")

    return when {
        values.size == 1 -> Pair(values.first(), emptyList())
        values.size > 1 -> Pair(values.first(), values.subList(1, values.size))
        else -> Pair(this, emptyList())
    }
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

fun String.maskSymbolsWith(mask: Char): String {
    val array = Array(length) { mask }
    return String(array.toCharArray())
}