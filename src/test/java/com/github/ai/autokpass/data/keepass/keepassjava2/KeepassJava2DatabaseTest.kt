package com.github.ai.autokpass.data.keepass.keepassjava2

import com.github.ai.autokpass.TestData.DB_WITH_BINARY_KEY
import com.github.ai.autokpass.TestData.DB_WITH_PASSWORD
import com.github.ai.autokpass.TestData.DB_WITH_XML_KEY
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class KeepassJava2DatabaseTest {

    @Test
    fun `getAllEntries should parse database correctly`() {
        // arrange
        val testDatabases = listOf(DB_WITH_PASSWORD, DB_WITH_BINARY_KEY, DB_WITH_XML_KEY)
        val expectedEntries = testDatabases.map { it.entries }

        // act
        val entries = testDatabases.map {
            KeepassJava2Database(it.loadDatabase()).getAllEntries()
        }

        // assert
        assertThat(entries).isEqualTo(expectedEntries)
    }
}
