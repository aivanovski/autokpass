package com.github.ai.autokpass.domain.window

interface FocusedWindowProvider {
    fun getFocusedWindow(): String?
}