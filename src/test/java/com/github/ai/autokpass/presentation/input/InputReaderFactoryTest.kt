package com.github.ai.autokpass.presentation.input

import com.github.ai.autokpass.model.InputReaderType
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class InputReaderFactoryTest {

    @Test
    fun `getInputReader should return InputReader by type`() {
        val factory = InputReaderFactory()

        assertThat(factory.getInputReader(InputReaderType.STANDARD))
            .isInstanceOf(StandardInputReader::class.java)

        assertThat(factory.getInputReader(InputReaderType.SECRET))
            .isInstanceOf(SecretInputReader::class.java)
    }
}