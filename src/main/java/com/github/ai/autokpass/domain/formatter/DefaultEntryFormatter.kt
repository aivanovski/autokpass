package com.github.ai.autokpass.domain.formatter

import com.github.ai.autokpass.model.KeepassEntry

class DefaultEntryFormatter : EntryFormatter {

    override fun format(entry: KeepassEntry): String {
        val sb = StringBuilder(entry.title.trim())

        if (entry.username.isNotBlank()) {
            sb.append(" ").append(entry.username.trim())

            if (entry.password.isNotBlank()) {
                sb.append(":").append(entry.password.maskWith('*'))
            }
        }

        return sb.toString()
    }

    private fun String.maskWith(symbol: Char): String {
        val chars = this.map { symbol }
        return String(chars.toCharArray())
    }
}