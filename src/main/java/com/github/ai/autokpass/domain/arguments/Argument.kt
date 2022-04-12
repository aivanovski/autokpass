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

    AUTOTYPE(
        fullName = "autotype",
        shortName = "a",
        description = "Program responsible for emulation of keyboard pressing (xdotool - for Linux, cliclick - for macOS)"
    ),

    INPUT(
        fullName = "input",
        shortName = "i",
        description = "Type of input (for debug purposes)"
    );

    val cliName: String = "--$fullName"
}