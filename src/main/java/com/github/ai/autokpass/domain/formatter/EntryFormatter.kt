package com.github.ai.autokpass.domain.formatter

import com.github.ai.autokpass.model.KeepassEntry

interface EntryFormatter {
    fun format(entry: KeepassEntry): String
}