package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.domain.printer.Printer
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.util.StringUtils.EMPTY
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

class ArgumentExtractor(
    private val printer: Printer
) {

    fun extractArguments(args: Array<String>): RawArgs {
        val parser = ArgParser("autokpass")

        val filePath by parser.option(
            ArgType.String,
            shortName = "f",
            fullName = "file-path",
            description = "Path to kdbx file"
        ).required()

        val selector by parser.option(
            ArgType.String,
            shortName = "s",
            fullName = "selector",
            description = "Selector to choose database entries. Possible values: 'stdout' or 'fzf'. Default value: 'stdout'"
        )

        val pattern by parser.option(
            ArgType.String,
            shortName = "p",
            fullName = "patterns",
            description = "Autotype sequence patterns, default value: '{USERNAME}{TAB}{PASSWORD}{ENTER}'"
        )

        parser.parse(args)

        printer.println("Enter a passphrase:")
        val password = readLine() ?: EMPTY

        return RawArgs(
            password,
            filePath,
            selector ?: EMPTY,
            pattern ?: EMPTY
        )
    }
}