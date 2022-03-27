package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.domain.arguments.Argument.DELAY_IN_SECONDS
import com.github.ai.autokpass.domain.arguments.Argument.FILE_PATH
import com.github.ai.autokpass.domain.arguments.Argument.LAUNCH_MODE
import com.github.ai.autokpass.domain.arguments.Argument.PASSWORD_AT_STD_IN
import com.github.ai.autokpass.domain.arguments.Argument.PATTERN
import com.github.ai.autokpass.domain.arguments.Argument.UID
import com.github.ai.autokpass.domain.printer.Printer
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.util.StringUtils.EMPTY
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

class ArgumentExtractor(
    private val printer: Printer
) {

    fun extractArguments(args: Array<String>): RawArgs {
        val parser = ArgParser("autokpass")

        val filePath by parser.option(
            ArgType.String,
            shortName = FILE_PATH.shortName,
            fullName = FILE_PATH.fullName,
            description = "Path to kdbx file"
        )

        val pattern by parser.option(
            ArgType.String,
            shortName = PATTERN.shortName,
            fullName = PATTERN.fullName,
            description = "Autotype sequence patterns, default value: '{USERNAME}{TAB}{PASSWORD}{ENTER}'"
        )

        val launchMode by parser.option(
            ArgType.String,
            shortName = LAUNCH_MODE.shortName,
            fullName = LAUNCH_MODE.fullName,
            description = ""
        )

        val uid by parser.option(
            ArgType.String,
            shortName = UID.shortName,
            fullName = UID.fullName,
            description = ""
        )

        val delayInSeconds by parser.option(
            ArgType.String,
            shortName = DELAY_IN_SECONDS.shortName,
            fullName = DELAY_IN_SECONDS.fullName,
            description = ""
        )

        val isPasswordAtStdIn by parser.option(
            ArgType.Boolean,
            shortName = PASSWORD_AT_STD_IN.shortName,
            fullName = PASSWORD_AT_STD_IN.fullName,
            description = ""
        )

        parser.parse(args)

        val password = if (isPasswordAtStdIn == true) {
            readLine() ?: EMPTY
        } else {
            printer.println("Enter a passphrase:")
            readLine() ?: EMPTY
        }

        return RawArgs(
            password,
            filePath ?: EMPTY,
            pattern ?: EMPTY,
            uid ?: EMPTY,
            delayInSeconds ?: EMPTY,
            launchMode ?: EMPTY
        )
    }
}