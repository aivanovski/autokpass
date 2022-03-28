package com.github.ai.autokpass.presentation.input

import com.github.ai.autokpass.util.StringUtils.EMPTY

class StandardInputReader : InputReader {

    override fun read(): String {
        return readLine() ?: EMPTY
    }
}