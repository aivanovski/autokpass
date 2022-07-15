package com.github.ai.autokpass.data.keepass.keepassjava2

import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.util.StringUtils
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase
import org.linguafranca.pwdb.kdbx.simple.SimpleEntry
import org.linguafranca.pwdb.kdbx.simple.SimpleGroup
import java.util.LinkedList

class KeepassJava2Database(
    private val db: SimpleDatabase
) : KeepassDatabase {

    override fun getAllEntries(): List<KeepassEntry> {
        return db.getAllEntries().toKeepassEntries()
    }

    private fun SimpleDatabase.getAllEntries(): List<SimpleEntry> {
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

    private fun SimpleEntry.toKeepassEntry(): KeepassEntry {
        return KeepassEntry(
            uid = uuid,
            title = title ?: StringUtils.EMPTY,
            username = username ?: StringUtils.EMPTY,
            password = password ?: StringUtils.EMPTY
        )
    }

    private fun List<SimpleEntry>.toKeepassEntries(): List<KeepassEntry> {
        return map { it.toKeepassEntry() }
    }
}