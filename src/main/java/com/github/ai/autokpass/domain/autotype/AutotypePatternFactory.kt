package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.PatternItemType
import kotlin.IllegalStateException

class AutotypePatternFactory {

    fun createPatternsForEntry(entry: KeepassEntry): List<AutotypePattern> {
        return AutotypePattern.ALL
            .filter { pattern -> isPatternApplicable(pattern, entry) }
    }

    private fun isPatternApplicable(pattern: AutotypePattern, entry: KeepassEntry): Boolean {
        val requiredItem = pattern.items
            .filter { item ->
                item == PatternItemType.USERNAME || item == PatternItemType.PASSWORD
            }

        for (item in requiredItem) {
            val value = when (item) {
                PatternItemType.USERNAME -> entry.username
                PatternItemType.PASSWORD -> entry.password
                else -> throw IllegalStateException()
            }

            if (value.isEmpty()) {
                return false
            }
        }

        return true
    }
}