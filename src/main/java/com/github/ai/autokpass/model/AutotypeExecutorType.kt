package com.github.ai.autokpass.model

enum class AutotypeExecutorType(val cliName: String) {
    XDOTOOL(cliName = "xdotool"),
    CLICLICK(cliName = "cliclick"),
    OSA_SCRIPT(cliName = "osascript")
}