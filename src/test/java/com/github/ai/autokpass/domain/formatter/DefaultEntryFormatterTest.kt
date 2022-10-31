package com.github.ai.autokpass.domain.formatter

import com.github.ai.autokpass.TestData.ENTRY1
import com.github.ai.autokpass.extensions.maskSymbolsWith
import com.github.ai.autokpass.util.StringUtils.EMPTY
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class DefaultEntryFormatterTest {

    @Test
    fun `format should create string with title, username and password`() {
        // arrange
        val entry = ENTRY1

        // act
        val result = DefaultEntryFormatter().format(entry)

        // assert
        assertThat(result).isEqualTo("${entry.title}: ${entry.username} - ${entry.password.maskSymbolsWith('*')}")
    }

    @Test
    fun `format should create string with title and username`() {
        // arrange
        val entry = ENTRY1.copy(password = EMPTY)

        // act
        val result = DefaultEntryFormatter().format(entry)

        // assert
        assertThat(result).isEqualTo("${entry.title}: ${entry.username}")
    }

    @Test
    fun `format should create string with title and password`() {
        // arrange
        val entry = ENTRY1.copy(username = EMPTY)

        // act
        val result = DefaultEntryFormatter().format(entry)

        // assert
        assertThat(result).isEqualTo("${entry.title}: ${entry.password.maskSymbolsWith('*')}")
    }

    @Test
    fun `format should create string with username and password`() {
        // arrange
        val entry = ENTRY1.copy(title = EMPTY)

        // act
        val result = DefaultEntryFormatter().format(entry)

        // assert
        assertThat(result).isEqualTo("${entry.username} - ${entry.password.maskSymbolsWith('*')}")
    }

    @Test
    fun `format should create string with title`() {
        // arrange
        val entry = ENTRY1.copy(username = EMPTY, password = EMPTY)

        // act
        val result = DefaultEntryFormatter().format(entry)

        // assert
        assertThat(result).isEqualTo(entry.title)
    }

    @Test
    fun `format should create string with username`() {
        // arrange
        val entry = ENTRY1.copy(title = EMPTY, password = EMPTY)

        // act
        val result = DefaultEntryFormatter().format(entry)

        // assert
        assertThat(result).isEqualTo(entry.username)
    }

    @Test
    fun `format should create string with password`() {
        // arrange
        val entry = ENTRY1.copy(title = EMPTY, username = EMPTY)

        // act
        val result = DefaultEntryFormatter().format(entry)

        // assert
        assertThat(result).isEqualTo(entry.password.maskSymbolsWith('*'))
    }
}