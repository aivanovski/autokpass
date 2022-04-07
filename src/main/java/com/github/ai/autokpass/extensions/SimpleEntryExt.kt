package com.github.ai.autokpass.extensions

import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.util.StringUtils.EMPTY
import org.linguafranca.pwdb.kdbx.simple.SimpleEntry

fun SimpleEntry.toKeepassEntry(): KeepassEntry {
    return KeepassEntry(
        uid = uuid,
        title = title ?: EMPTY,
        username = username ?: EMPTY,
        password = password ?: EMPTY
    )
}

fun List<SimpleEntry>.toKeepassEntries(): List<KeepassEntry> {
    return map { it.toKeepassEntry() }
}