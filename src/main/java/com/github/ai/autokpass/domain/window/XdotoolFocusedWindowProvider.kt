package com.github.ai.autokpass.domain.window

import com.github.ai.autokpass.presentation.process.ProcessExecutor

class XdotoolFocusedWindowProvider(
    private val processExecutor: ProcessExecutor
) : FocusedWindowProvider {

    override fun getFocusedWindow(): String? {
        val getWindowNameResult = processExecutor.execute("xdotool getactivewindow getwindowname")

        val windowName = if (getWindowNameResult.isSucceeded() &&
            getWindowNameResult.getDataOrThrow().isNotEmpty()
        ) {
            getWindowNameResult.getDataOrThrow()
        } else {
            null
        }

        return windowName
    }
}