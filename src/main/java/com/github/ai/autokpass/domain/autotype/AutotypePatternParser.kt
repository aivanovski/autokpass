package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.PatternItemType

class AutotypePatternParser {

    fun parse(data: String): AutotypePattern? {
        val items = data.split("{")
            .map { it.replace("}", "").trim() }
            .filter { it.isNotEmpty() }
            .map { PatternItemType.getByName(it) }

        return if (items.isNotEmpty() && items.all { it != null }) {
            AutotypePattern(items.mapNotNull { it })
        } else {
            null
        }
    }
}