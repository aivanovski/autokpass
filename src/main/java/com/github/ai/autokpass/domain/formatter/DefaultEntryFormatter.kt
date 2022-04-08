package com.github.ai.autokpass.domain.formatter

import com.github.ai.autokpass.extensions.maskSymbolsWith
import com.github.ai.autokpass.model.KeepassEntry

class DefaultEntryFormatter : EntryFormatter {

    override fun format(entry: KeepassEntry): String {
        return StringBuilder()
            .apply {
                if (entry.title.isNotBlank()) {
                    append(entry.title)
                }

                if (entry.username.isNotBlank()) {
                    if (length > 0) {
                        append(": ")
                    }
                    append(entry.username.trim())
                }

                if (entry.password.isNotBlank()) {
                    if (length > 0 && entry.username.isNotBlank()) {
                        append(" - ")
                    } else if (length > 0) {
                        append(": ")
                    }
                    append(entry.password.trim().maskSymbolsWith('*'))
                }
            }
            .toString()
    }
}