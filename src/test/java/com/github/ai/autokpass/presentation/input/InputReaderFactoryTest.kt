package com.github.ai.autokpass.presentation.input

import com.github.ai.autokpass.model.InputReaderType.SECRET
import com.github.ai.autokpass.model.InputReaderType.STANDARD
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import org.junit.jupiter.api.Test

class InputReaderFactoryTest {

    @Test
    fun `getInputReader should return InputReader by type`() {
        val factory = InputReaderFactory()

        factory.getInputReader(STANDARD) should beInstanceOf<StandardInputReader>()
        factory.getInputReader(SECRET) should beInstanceOf<SecretInputReader>()
    }
}