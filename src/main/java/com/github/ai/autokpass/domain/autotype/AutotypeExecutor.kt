package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeSequence

interface AutotypeExecutor {
    // TODO: should return result
    fun execute(sequence: AutotypeSequence)
}