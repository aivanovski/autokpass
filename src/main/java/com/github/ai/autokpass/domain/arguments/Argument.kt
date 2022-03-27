package com.github.ai.autokpass.domain.arguments

enum class Argument(val fullName: String, val shortName: String) {

    FILE_PATH(
        fullName = "file-path",
        shortName = "f"
    ),

    PATTERN(
        fullName = "pattern",
        shortName = "p"
    ),

    LAUNCH_MODE(
        fullName = "launch-mode",
        shortName = "l"
    ),

    UID(
        fullName = "uid",
        shortName = "u"
    ),

    PASSWORD_AT_STD_IN(
        fullName = "password-at-std-in",
        shortName = "s"
    ),

    DELAY_IN_SECONDS(
        fullName = "delay-in-sec",
        shortName = "d"
    );

    val cliName: String = "--$fullName"
}