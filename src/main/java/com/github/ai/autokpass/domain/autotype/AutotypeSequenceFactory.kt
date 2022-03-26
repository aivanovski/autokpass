package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.AutotypeSequenceItem
import com.github.ai.autokpass.model.KeepassEntry

class AutotypeSequenceFactory {

    fun createAutotypeSequence(entry: KeepassEntry): AutotypeSequence? {
        // TODO: implement with pattern
        val items = mutableListOf<AutotypeSequenceItem>()

        if (entry.username.isNotBlank()) {
            items.add(AutotypeSequenceItem.Text(entry.username.trim()))
        }

        if (entry.password.isNotBlank()) {
            if (items.isNotEmpty()) {
                items.add(AutotypeSequenceItem.Tab)
                items.add(AutotypeSequenceItem.Delay(DEFAULT_DELAY_BETWEEN_ACTIONS))
            }

            items.add(AutotypeSequenceItem.Text(entry.password.trim()))
        }

        return if (items.isNotEmpty()) {
            items.add(AutotypeSequenceItem.Delay(DEFAULT_DELAY_BETWEEN_ACTIONS))
            items.add(AutotypeSequenceItem.Enter)

            AutotypeSequence(items)
        } else {
            null
        }
    }

    companion object {
        private const val DEFAULT_DELAY_BETWEEN_ACTIONS = 200L
    }
}