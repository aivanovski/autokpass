package com.github.ai.autokpass.extensions

import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase
import org.linguafranca.pwdb.kdbx.simple.SimpleEntry
import org.linguafranca.pwdb.kdbx.simple.SimpleGroup
import java.util.LinkedList

fun SimpleDatabase.getAllEntries(): List<SimpleEntry> {
    val result = mutableListOf<SimpleEntry>()

    val nextGroups = LinkedList<SimpleGroup>()
        .apply {
            add(rootGroup)
        }

    while (nextGroups.isNotEmpty()) {
        val group = nextGroups.pollFirst()
        nextGroups.addAll(group.groups)
        result.addAll(group.entries)
    }

    return result
}