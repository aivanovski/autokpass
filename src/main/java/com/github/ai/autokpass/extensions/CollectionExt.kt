package com.github.ai.autokpass.extensions

import java.util.LinkedHashMap

fun <K, V> List<Pair<K, V>>.toLinkedHashMap(): LinkedHashMap<K, V> {
    val map = LinkedHashMap<K, V>()

    for ((key, value) in this) {
        map[key] = value
    }

    return map
}