package com.github.ai.autokpass.data.keepass.kotpass

import com.github.ai.autokpass.TestData.DB_WITH_BINARY_KEY
import com.github.ai.autokpass.TestData.DB_WITH_PASSWORD
import com.github.ai.autokpass.loadKotpassDatabase
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class KotpassDatabaseTest {

    @Test
    fun `getAllEntries should parse database correctly`() {
        // arrange
        val testDatabases = listOf(DB_WITH_PASSWORD, DB_WITH_BINARY_KEY)
        val expectedEntries = testDatabases
            .map { db -> db.entries.sortedBy { it.uid } }

        // act
        val entries = testDatabases.map { db ->
            KotpassDatabase(db.loadKotpassDatabase())
                .getAllEntries()
                .sortedBy { it.uid }
        }

        // assert
        assertThat(entries).isEqualTo(expectedEntries)
    }
}