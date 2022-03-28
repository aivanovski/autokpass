package com.github.ai.autokpass.domain.formatter

import com.github.ai.autokpass.model.KeepassEntry

class DefaultEntryFormatter : EntryFormatter {

    override fun format(entry: KeepassEntry): String {
        return StringBuilder()
            .apply {
                append(entry.title).append(": ")

                if (entry.username.isNotBlank()) {
                    append(" ").append(entry.username.trim())

                    if (entry.password.isNotBlank()) {
                        append(" - ").append(entry.password.maskWith('*'))
                    }
                }
            }
            .toString()
    }

    private fun String.maskWith(symbol: Char): String {
        val chars = this.map { symbol }
        return String(chars.toCharArray())
    }
}