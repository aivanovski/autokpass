package com.github.ai.autokpass.domain

object Errors {

    const val FAILED_TO_COMPILE_AUTOTYPE_SEQUENCE = "Failed to compile autotype sequence"
    const val ERROR_HAS_BEEN_OCCURRED = "Error has been occurred"

    // Argument parsing error
    const val GENERIC_EMPTY_ARGUMENT = "Option %s can't be empty"
    const val GENERIC_FILE_DOES_NOT_EXIST = "Specified file doesn't exist: %s"
    const val GENERIC_FILE_IS_NOT_A_FILE = "Specified file is not a file: %s"
    const val GENERIC_FAILED_TO_PARSE_ARGUMENT = "Failed to parse %s value: %s"
    const val GENERIC_FAILED_TO_GET_VALUE_FOR_VARIABLE = "Failed to get value for environment variable: %s"
}