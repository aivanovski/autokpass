package com.github.ai.autokpass.model

import com.github.ai.autokpass.domain.arguments.Argument
import com.github.ai.autokpass.extensions.getDefaultAsLong
import com.github.ai.autokpass.util.StringUtils

data class ParsedArgs(
    val filePath: String,
    val keyPath: String?,
    val startDelayInMillis: Long,
    val delayBetweenActionsInMillis: Long,
    val autotypeType: AutotypeExecutorType?,
    val keyProcessingCommand: String?
) {

    companion object {
        val EMPTY = ParsedArgs(
            filePath = StringUtils.EMPTY,
            keyPath = null,
            startDelayInMillis = Argument.DELAY.getDefaultAsLong(),
            delayBetweenActionsInMillis = Argument.AUTOTYPE_DELAY.getDefaultAsLong(),
            autotypeType = null,
            keyProcessingCommand = null
        )
    }
}