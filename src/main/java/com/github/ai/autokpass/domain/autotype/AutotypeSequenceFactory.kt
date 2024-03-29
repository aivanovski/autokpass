package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.AutotypeSequenceItem
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.PatternItemType

class AutotypeSequenceFactory {

    fun createAutotypeSequence(
        entry: KeepassEntry,
        pattern: AutotypePattern,
        delayBetweenActionsInMillis: Long
    ): AutotypeSequence? {
        val items = mutableListOf<AutotypeSequenceItem>()

        for (item in filterPatternItems(entry, pattern)) {
            when (item) {
                PatternItemType.USERNAME -> {
                    if (entry.username.isNotBlank()) {
                        if (items.isNotEmpty()) {
                            items.add(AutotypeSequenceItem.Delay(delayBetweenActionsInMillis))
                        }
                        items.add(AutotypeSequenceItem.Text(entry.username.trim()))
                    }
                }
                PatternItemType.PASSWORD -> {
                    if (entry.password.isNotBlank()) {
                        if (items.isNotEmpty()) {
                            items.add(AutotypeSequenceItem.Delay(delayBetweenActionsInMillis))
                        }
                        items.add(AutotypeSequenceItem.Text(entry.password.trim()))
                    }
                }
                PatternItemType.ENTER -> {
                    if (items.isNotEmpty()) {
                        items.add(AutotypeSequenceItem.Delay(delayBetweenActionsInMillis))
                        items.add(AutotypeSequenceItem.Enter)
                    }
                }
                PatternItemType.TAB -> {
                    if (items.isNotEmpty()) {
                        items.add(AutotypeSequenceItem.Delay(delayBetweenActionsInMillis))
                        items.add(AutotypeSequenceItem.Tab)
                    }
                }
            }
        }

        return if (items.isNotEmpty()) {
            AutotypeSequence(items)
        } else {
            null
        }
    }

    private fun filterPatternItems(
        entry: KeepassEntry,
        pattern: AutotypePattern
    ): List<PatternItemType> {
        val filteredItems = mutableListOf<PatternItemType>()
        val items = pattern.items

        var idx = 0
        while (idx < items.size) {
            when (val item = items[idx]) {
                PatternItemType.USERNAME -> {
                    if (entry.username.isNotBlank()) {
                        filteredItems.add(item)
                    } else {
                        val nextItem = if (idx + 1 < items.size) items[idx + 1] else null
                        if (nextItem == PatternItemType.TAB || nextItem == PatternItemType.ENTER) {
                            idx += 2
                            continue
                        }
                    }
                }
                PatternItemType.PASSWORD -> {
                    if (entry.password.isNotBlank()) {
                        filteredItems.add(item)
                    } else {
                        val nextItem = if (idx + 1 < items.size) items[idx + 1] else null
                        if (nextItem == PatternItemType.TAB || nextItem == PatternItemType.ENTER) {
                            idx += 2
                            continue
                        }
                    }
                }
                PatternItemType.TAB -> {
                    filteredItems.add(item)
                }
                PatternItemType.ENTER -> {
                    filteredItems.add(item)
                }
            }

            idx++
        }

        if (filteredItems.isNotEmpty()) {
            if (filteredItems.last() != items.last() && items.last() == PatternItemType.ENTER) {
                filteredItems[filteredItems.lastIndex] = items.last()
            }
        }

        return filteredItems
    }
}