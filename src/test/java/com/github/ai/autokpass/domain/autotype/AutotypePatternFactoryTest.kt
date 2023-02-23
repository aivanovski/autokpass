package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.TestData
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AutotypePatternFactoryTest {

    @Test
    fun `createPatternsForEntry should return all default patterns`() {
        // arrange
        val entry = newEntry(username = USERNAME, password = PASSWORD)

        // act
        val patterns = AutotypePatternFactory()
            .createPatternsForEntry(entry)

        // assert
        patterns shouldBe AutotypePattern.ALL
    }

    @Test
    fun `createPatternsForEntry should return empty list`() {
        // arrange
        val entry = newEntry(username = EMPTY, password = EMPTY)

        // act
        val patterns = AutotypePatternFactory()
            .createPatternsForEntry(entry)

        // assert
        patterns shouldBe emptyList()
    }

    @Test
    fun `createPatternsForEntry should return patterns without Username`() {
        // arrange
        val expected = listOf(
            AutotypePattern.PASSWORD_WITH_ENTER,
            AutotypePattern.PASSWORD
        )
        val entry = newEntry(username = EMPTY, password = PASSWORD)

        // act
        val patterns = AutotypePatternFactory()
            .createPatternsForEntry(entry)

        // assert
        patterns shouldBe expected
    }

    @Test
    fun `createPatternsForEntry should return patterns without Password`() {
        // arrange
        val expected = listOf(
            AutotypePattern.USERNAME_WITH_ENTER,
            AutotypePattern.USERNAME
        )
        val entry = newEntry(username = USERNAME, password = EMPTY)

        // act
        val patterns = AutotypePatternFactory()
            .createPatternsForEntry(entry)

        // assert
        patterns shouldBe expected
    }

    private fun newEntry(username: String, password: String): KeepassEntry =
        TestData.ENTRY1.copy(
            username = username,
            password = password
        )

    companion object {
        private const val USERNAME = "username"
        private const val PASSWORD = "password"
    }
}