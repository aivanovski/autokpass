package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.model.Result

interface ArgumentExtractor {
    fun extractArguments(): Result<RawArgs>
}