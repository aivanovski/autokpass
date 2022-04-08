package com.github.ai.autokpass.extensions

import com.github.ai.autokpass.TestData.ENTRY1
import com.github.ai.autokpass.TestData.ENTRY2
import com.github.ai.autokpass.model.KeepassEntry
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.linguafranca.pwdb.kdbx.simple.SimpleEntry

class SimpleEntryExtKtTest {

    @Test
    fun `toKeepassEntry should convert entries`() {
        // arrange
        val entry = mockSimpleEntry(ENTRY1)

        // act
        val result = entry.toKeepassEntry()

        // assert
        assertThat(result).isEqualTo(ENTRY1)
    }

    @Test
    fun `toKeepassEntries should convert list of entries`() {
        // arrange
        val entries = listOf(
            mockSimpleEntry(ENTRY1),
            mockSimpleEntry(ENTRY2)
        )

        // act
        val result = entries.toKeepassEntries()

        // assert
        assertThat(result).isEqualTo(listOf(ENTRY1, ENTRY2))
    }

    private fun mockSimpleEntry(source: KeepassEntry): SimpleEntry {
        return mockk<SimpleEntry>().apply {
            every { uuid }.returns(source.uid)
            every { title }.returns(source.title)
            every { username }.returns(source.username)
            every { password }.returns(source.password)
        }
    }
}