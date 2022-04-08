package com.github.ai.autokpass.extensions

import com.github.ai.autokpass.TestData.ENTRY1
import com.github.ai.autokpass.TestData.ENTRY2
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase
import org.linguafranca.pwdb.kdbx.simple.SimpleEntry
import java.util.UUID

class SimpleDatabaseExtKtTest {

    @Test
    fun `getAllEntries should return all entries from db`() {
        // arrange
        val db = SimpleDatabase().apply {
            val group = newGroup(GROUP_TITLE).apply {
                addEntry(newEntry(ENTRY1.title))
                addEntry(newEntry(ENTRY2.title))
            }

            rootGroup.addGroup(group)
        }

        // act
        val entries = db.getAllEntries()

        // assert
        assertThat(entries.size).isEqualTo(2)

        val uids = entries.mapNotNull { it.uuid as UUID? }
        assertThat(uids.size).isEqualTo(2)

        val titles = entries.map { it.title }.sorted()
        assertThat(titles).isEqualTo(listOf(ENTRY1.title, ENTRY2.title))
    }

    companion object {
        private const val GROUP_TITLE = "group"
    }
}