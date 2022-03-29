package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.AutotypeSequenceItem
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AutotypeSequenceSerializationTest {

    @Test
    fun `format should serialize all items`() {
        // arrange
        val sequence = AutotypeSequence(
            items = listOf(
                AutotypeSequenceItem.Text(TEXT1),
                AutotypeSequenceItem.Delay(DELAY),
                AutotypeSequenceItem.Tab,
                AutotypeSequenceItem.Delay(DELAY),
                AutotypeSequenceItem.Text(TEXT2),
                AutotypeSequenceItem.Delay(DELAY),
                AutotypeSequenceItem.Enter
            )
        )

        // act
        val json = AutotypeSequenceFormatter().format(sequence)
        requireNotNull(json)

        val result = AutotypeSequenceParser().parse(json)

        // assert
        assertThat(result).isEqualTo(sequence)
    }

    @Test
    fun `format should return null`() {
        // arrange
        val sequence = AutotypeSequence(items = emptyList())

        // act
        val result = AutotypeSequenceFormatter().format(sequence)

        // assert
        assertThat(result).isNull()
    }

    @Test
    fun `passe should return null`() {
        // arrange
        val json = ""

        // act
        val result = AutotypeSequenceParser().parse(json)

        // assert
        assertThat(result).isNull()
    }

    companion object {
        private const val TEXT1 = "text1"
        private const val TEXT2 = "text2"
        private const val DELAY = 200L
    }
}