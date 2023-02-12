package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.arguments.Argument

object Errors {

    val FAILED_TO_DETERMINE_AUTOTYPE_EXECUTOR_TYPE = """
        Failed to determine program for autotype, please specify it explicitly 
        with ${Argument.AUTOTYPE.cliName} option
    """.trimIndent()

    const val FAILED_TO_DETERMINE_OS_TYPE = "Can't determine OS type"
    const val FAILED_TO_COMPILE_AUTOTYPE_SEQUENCE = "Failed to compile autotype sequence"
    const val TOO_MANY_ATTEMPTS = "Too many attempts"
    const val INVALID_PASSWORD_MESSAGE = "Invalid password, please enter a password again:"
    const val ERROR_HAS_BEEN_OCCURRED = "Error has been occurred"

    // Argument parsing error
    const val GENERIC_EMPTY_ARGUMENT = "Option %s can't be empty"
    const val GENERIC_FILE_DOES_NOT_EXIST = "Specified file doesn't exist: %s"
    const val GENERIC_FILE_IS_NOT_A_FILE = "Specified file is not a file: %s"
    const val GENERIC_FAILED_TO_PARSE_ARGUMENT = "Failed to parse %s value: %s"
    const val GENERIC_FAILED_TO_GET_VALUE_FOR_VARIABLE = "Failed to get value for environment variable: %s"
}