package com.github.ai.autokpass.presentation.input

import com.github.ai.autokpass.model.InputReaderType

class InputReaderFactory {
    fun getInputReader(type: InputReaderType): InputReader {
        return when(type) {
            InputReaderType.STANDARD -> StandardInputReader()
            InputReaderType.SECRET -> SecretInputReader()
        }
    }
}