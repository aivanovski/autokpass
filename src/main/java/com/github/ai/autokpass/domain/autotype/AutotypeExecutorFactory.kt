package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.presentation.process.ProcessExecutor

class AutotypeExecutorFactory(
    private val processExecutor: ProcessExecutor,
    private val threadThrottler: ThreadThrottler
) {

    fun getExecutor(type: AutotypeExecutorType): AutotypeExecutor {
        return when (type) {
            AutotypeExecutorType.XDOTOOL -> XdotoolAutotypeExecutor(
                processExecutor,
                threadThrottler
            )
            AutotypeExecutorType.CLICLICK -> CliclickAutotypeExecutor(
                processExecutor,
                threadThrottler
            )
            AutotypeExecutorType.OSA_SCRIPT -> OsaScriptAutotypeExecutor(
                processExecutor,
                threadThrottler
            )
        }
    }
}