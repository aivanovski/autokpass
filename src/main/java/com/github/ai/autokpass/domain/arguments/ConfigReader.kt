package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.model.RawConfig
import com.github.ai.autokpass.model.Result

interface ConfigReader {
    fun readConfig(): Result<RawConfig>
}