package com.github.ai.autokpass.extensions

import com.github.ai.autokpass.model.KeepassEntry
import org.linguafranca.pwdb.kdbx.simple.SimpleEntry

fun SimpleEntry.toKeepassEntry(): KeepassEntry {
    return KeepassEntry(
        uid = uuid,
        title = title,
        username = username,
        password = password
    )
}

fun List<SimpleEntry>.toKeepassEntries(): List<KeepassEntry> {
    return map { it.toKeepassEntry() }
}