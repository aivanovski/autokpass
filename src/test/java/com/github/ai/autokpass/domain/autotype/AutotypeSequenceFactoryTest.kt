package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory.Companion.DEFAULT_DELAY_BETWEEN_ACTIONS
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.AutotypeSequenceItem
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.util.StringUtils.EMPTY
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.UUID

class AutotypeSequenceFactoryTest {

    @Test
    fun `createAutotypeSequence should return sequence with username and password for default pattern`() {
        // arrange
        val entry = createEntry(username = USERNAME, password = PASSWORD)
        val sequenceItems = listOf(
            AutotypeSequenceItem.Text(USERNAME),
            AutotypeSequenceItem.Delay(DEFAULT_DELAY_BETWEEN_ACTIONS),
            AutotypeSequenceItem.Tab,
            AutotypeSequenceItem.Delay(DEFAULT_DELAY_BETWEEN_ACTIONS),
            AutotypeSequenceItem.Text(PASSWORD),
            AutotypeSequenceItem.Delay(DEFAULT_DELAY_BETWEEN_ACTIONS),
            AutotypeSequenceItem.Enter
        )

        // act
        val sequence = AutotypeSequenceFactory().createAutotypeSequence(
            entry,
            pattern = AutotypePattern.DEFAULT_PATTERN
        )

        // assert
        assertThat(sequence).isNotNull()
        assertThat(sequence?.items).isEqualTo(sequenceItems)
    }

    @Test
    fun `createAutotypeSequence should return sequence with username for default pattern`() {
        // arrange
        val entry = createEntry(username = USERNAME)
        val sequenceItems = listOf(
            AutotypeSequenceItem.Text(USERNAME),
            AutotypeSequenceItem.Delay(DEFAULT_DELAY_BETWEEN_ACTIONS),
            AutotypeSequenceItem.Enter
        )

        // act
        val sequence = AutotypeSequenceFactory().createAutotypeSequence(
            entry,
            pattern = AutotypePattern.DEFAULT_PATTERN
        )

        // assert
        assertThat(sequence).isNotNull()
        assertThat(sequence?.items).isEqualTo(sequenceItems)
    }

    @Test
    fun `createAutotypeSequence should return sequence with password for default pattern`() {
        // arrange
        val entry = createEntry(password = PASSWORD)
        val sequenceItems = listOf(
            AutotypeSequenceItem.Text(PASSWORD),
            AutotypeSequenceItem.Delay(DEFAULT_DELAY_BETWEEN_ACTIONS),
            AutotypeSequenceItem.Enter
        )

        // act
        val sequence = AutotypeSequenceFactory().createAutotypeSequence(
            entry,
            pattern = AutotypePattern.DEFAULT_PATTERN
        )

        // assert
        assertThat(sequence).isNotNull()
        assertThat(sequence?.items).isEqualTo(sequenceItems)
    }

    @Test
    fun `createAutotypeSequence should return sequence with username`() {
        // arrange
        val entry = createEntry(username = USERNAME)
        val sequenceItems = listOf(
            AutotypeSequenceItem.Text(USERNAME),
            AutotypeSequenceItem.Delay(DEFAULT_DELAY_BETWEEN_ACTIONS),
            AutotypeSequenceItem.Enter
        )

        // act
        val sequence = AutotypeSequenceFactory().createAutotypeSequence(
            entry,
            pattern = AutotypePattern.USERNAME_WITH_ENTER
        )

        // assert
        assertThat(sequence).isNotNull()
        assertThat(sequence?.items).isEqualTo(sequenceItems)
    }

    @Test
    fun `createAutotypeSequence should return sequence with password`() {
        // arrange
        val entry = createEntry(password = PASSWORD)
        val sequenceItems = listOf(
            AutotypeSequenceItem.Text(PASSWORD),
            AutotypeSequenceItem.Delay(DEFAULT_DELAY_BETWEEN_ACTIONS),
            AutotypeSequenceItem.Enter
        )

        // act
        val sequence = AutotypeSequenceFactory().createAutotypeSequence(
            entry,
            pattern = AutotypePattern.PASSWORD_WITH_ENTER
        )

        // assert
        assertThat(sequence).isNotNull()
        assertThat(sequence?.items).isEqualTo(sequenceItems)
    }

    @Test
    fun `createAutotypeSequence should return null`() {
        // arrange
        val entry = createEntry()

        // act
        val sequence = AutotypeSequenceFactory().createAutotypeSequence(
            entry,
            pattern = AutotypePattern.PASSWORD_WITH_ENTER
        )

        // assert
        assertThat(sequence).isNull()
    }

    private fun createEntry(
        username: String = EMPTY,
        password: String = EMPTY
    ): KeepassEntry =
        KeepassEntry(
            uid = UID,
            title = "title",
            username = username,
            password = password
        )

    companion object {
        private const val USERNAME = "username"
        private const val PASSWORD = "password"
        private val UID = UUID(100, 100)
    }
}
