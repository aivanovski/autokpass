package com.github.ai.autokpass.domain.arguments

enum class Argument(
    val fullName: String,
    val shortName: String,
    val description: String
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

    XML_KEY(
        fullName = "xml-key",
        shortName = "x",
        description = "Interpret key file as xml file"
    ),

    DELAY(
        fullName = "delay",
        shortName = "d",
        description = "Delay in seconds before autotype will be started"
    ),

    AUTOTYPE_DELAY(
        fullName = "autotype-delay",
        shortName = "b",
        description = "Delay in milliseconds between autotype actions"
    ),

    AUTOTYPE(
        fullName = "autotype",
        shortName = "a",
        description = """Program responsible for emulation of keyboard pressing, available options:
            xdotool - default for Linux (xdotool should be installed on the host machine)
            cliclick - default for macOS (cliclick should be installed on the host machine)
            osascript - optional for macOS (keyboard pressing will be emulated via AppleScript)
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
}