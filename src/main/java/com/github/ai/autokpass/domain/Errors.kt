package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.arguments.Argument

object Errors {

    val FAILED_TO_DETERMINE_AUTOTYPE_EXECUTOR_TYPE = """
        Failed to determine program for autotype, please specify it explicitly 
        with ${Argument.AUTOTYPE.cliName} option
    """.trimIndent()

    const val FAILED_TO_DETERMINE_OS_TYPE = "Can't determine OS type"

    // Argument parsing error
    const val GENERIC_EMPTY_ARGUMENT = "Option %s can't be empty"
    const val GENERIC_FILE_DOES_NOT_EXIST = "Specified file doesn't exist: %s"
    const val GENERIC_FILE_IS_NOT_A_FILE = "Specified file is not a file: %s"
    const val GENERIC_FAILED_TO_PARSE_ARGUMENT = "Failed to parse %s value: %s"
}