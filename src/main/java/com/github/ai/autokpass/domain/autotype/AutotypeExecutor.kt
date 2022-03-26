package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeSequence

interface AutotypeExecutor {
    fun execute(sequence: AutotypeSequence)
}