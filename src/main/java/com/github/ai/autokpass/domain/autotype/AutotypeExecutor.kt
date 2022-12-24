package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.Result

interface AutotypeExecutor {
    fun execute(sequence: AutotypeSequence): Result<Unit>
}