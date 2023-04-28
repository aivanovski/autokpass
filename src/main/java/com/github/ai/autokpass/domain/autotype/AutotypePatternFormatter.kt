package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypePattern

class AutotypePatternFormatter {

    fun format(pattern: AutotypePattern): String {
        val items = pattern.items.map { item -> "{${item.name}}" }
        return items.joinToString(separator = "")
    }
}