package com.github.ai.autokpass.model

import com.github.ai.autokpass.domain.arguments.Argument
import com.github.ai.autokpass.extensions.getDefaultAsLong
import com.github.ai.autokpass.util.StringUtils

data class ParsedConfig(
    val filePath: String,
    val keyPath: String?,
    val startDelayInMillis: Long,
    val delayBetweenActionsInMillis: Long,
    val autotypeType: AutotypeExecutorType?,
    val keyProcessingCommand: String?
) {

    companion object {
        val EMPTY = ParsedConfig(
            filePath = StringUtils.EMPTY,
            keyPath = null,
            startDelayInMillis = Argument.DELAY.getDefaultAsLong(),
            delayBetweenActionsInMillis = Argument.AUTOTYPE_DELAY.getDefaultAsLong(),
            autotypeType = null,
            keyProcessingCommand = null
        )
    }
}