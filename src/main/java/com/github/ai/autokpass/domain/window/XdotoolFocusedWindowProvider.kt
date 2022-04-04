package com.github.ai.autokpass.domain.window

import com.github.ai.autokpass.presentation.process.ProcessExecutor

class XdotoolFocusedWindowProvider(
    private val processExecutor: ProcessExecutor
) : FocusedWindowProvider {

    override fun getFocusedWindow(): String? {
        val windowName = processExecutor.execute("xdotool getactivewindow getwindowname")
        return windowName.ifBlank { null }
    }
}