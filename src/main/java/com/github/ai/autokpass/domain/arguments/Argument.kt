package com.github.ai.autokpass.domain.arguments

enum class Argument(
    val fullName: String,
    val shortName: String,
    val description: String,
    val defaultValue: String? = null
) {

    FILE(
        fullName = "file",
        shortName = "f",
        description = "Path to KeePass database"
    ),

    KEY_FILE(
        fullName = "key-file",
        shortName = "k",
        description = "Path to key file"
    ),

    DELAY(
        fullName = "delay",
        shortName = "d",
        defaultValue = "3000",
        description = "Delay in seconds or milliseconds before autotype will be started. Default value - 3 seconds"
    ),

    AUTOTYPE_DELAY(
        fullName = "autotype-delay",
        shortName = "b",
        defaultValue = "200",
        description = "Delay seconds or milliseconds between autotype actions. Default value - 200 milliseconds"
    ),

    AUTOTYPE(
        fullName = "autotype",
        shortName = "a",
        description = """Program responsible for emulation of keyboard pressing, available options:
            xdotool - default for Linux (xdotool should be installed on the host machine)
            osascript - default for macOS (keyboard pressing will be emulated via AppleScript)
            cliclick - optional for macOS (cliclick should be installed on the host machine)
            """
    ),

    INPUT(
        fullName = "input",
        shortName = "i",
        description = "Type of input (for debug purposes)"
    ),

    PROCESS_KEY_COMMAND(
        fullName = "process-key-command",
        shortName = "c",
        description = "Executes shell commands on file specified in --key-file and uses it to unlock database"
    );

    val cliName: String = "--$fullName"
    val cliShortName: String = "-$shortName"
}