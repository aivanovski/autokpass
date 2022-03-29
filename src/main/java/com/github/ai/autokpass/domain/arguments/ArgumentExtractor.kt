package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.domain.arguments.Argument.AUTOTYPE_SEQUENCE
import com.github.ai.autokpass.domain.arguments.Argument.DELAY
import com.github.ai.autokpass.domain.arguments.Argument.FILE
import com.github.ai.autokpass.domain.arguments.Argument.INPUT
import com.github.ai.autokpass.domain.arguments.Argument.SINGLE_PROCESS
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.util.StringUtils.EMPTY
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

class ArgumentExtractor {

    fun extractArguments(args: Array<String>): RawArgs {
        val parser = ArgParser("autokpass")

        val filePath by parser.option(
            ArgType.String,
            shortName = FILE.shortName,
            fullName = FILE.fullName,
            description = FILE.description
        )

        val delayInSeconds by parser.option(
            ArgType.String,
            shortName = DELAY.shortName,
            fullName = DELAY.fullName,
            description = DELAY.description
        )

        val inputReader by parser.option(
            ArgType.String,
            shortName = INPUT.shortName,
            fullName = INPUT.fullName,
            description = INPUT.description
        )

        val isSingleProcess by parser.option(
            ArgType.Boolean,
            shortName = SINGLE_PROCESS.shortName,
            fullName = SINGLE_PROCESS.fullName,
            description = SINGLE_PROCESS.description
        )

        val autotypeSequence by parser.option(
            ArgType.String,
            shortName = AUTOTYPE_SEQUENCE.shortName,
            fullName = AUTOTYPE_SEQUENCE.fullName,
            description = AUTOTYPE_SEQUENCE.description
        )

        parser.parse(args)

        return RawArgs(
            filePath ?: EMPTY,
            delayInSeconds ?: EMPTY,
            inputReader ?: EMPTY,
            autotypeSequence ?: EMPTY,
            isSingleProcess ?: false
        )
    }
}