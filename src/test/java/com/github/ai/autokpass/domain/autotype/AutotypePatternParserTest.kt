package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypePattern
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AutotypePatternParserTest {

    @Test
    fun `parse should return valid pattern`() {
        // arrange
        val input = listOf(
             "{USERNAME}{TAB}{PASSWORD}{ENTER}",
             "{USERNAME}{ENTER}",
             "{PASSWORD}{ENTER}"
        )

        val patterns = listOf(
            AutotypePattern.DEFAULT_PATTERN,
            AutotypePattern.USERNAME_WITH_ENTER,
            AutotypePattern.PASSWORD_WITH_ENTER
        )

        // act
        val parser = AutotypePatternParser()
        val results = input.map { parser.parse(it) }

        // assert
        assertThat(results).isEqualTo(patterns)
    }

    @Test
    fun `parse should return null`() {
        // arrange
        val input = listOf(
            "{USERNAME}{ABC}",
            "",
            "{}{}"
        )

        // act
        val parser = AutotypePatternParser()
        val result = input
            .map { parser.parse(it) }
            .all { it == null }

        // assert
        assertThat(result).isTrue()
    }
}