package com.github.ai.autokpass.model

enum class AutotypeExecutorType(val cliName: String) {
    XDOTOOL(cliName = "xdotool"),
    OSA_SCRIPT(cliName = "osascript"),
    CLICLICK(cliName = "cliclick")
}