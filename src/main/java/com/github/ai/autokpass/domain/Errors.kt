package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.arguments.Argument

object Errors {

    val FAILED_TO_DETERMINE_AUTOTYPE_EXECUTOR_TYPE = """
        Failed to determine program for autotype, please specify it explicitly 
        with ${Argument.AUTOTYPE.cliName} option
    """.trimIndent()

    const val FAILED_TO_DETERMINE_OS_TYPE = "Can't determine OS type"
}