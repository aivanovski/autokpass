package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.domain.arguments.Argument.DELAY
import com.github.ai.autokpass.domain.arguments.Argument.FILE
import com.github.ai.autokpass.domain.arguments.Argument.INPUT
import com.github.ai.autokpass.domain.arguments.Argument.KEY_FILE
import com.github.ai.autokpass.domain.arguments.Argument.XML_KEY
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

        val keyPath by parser.option(
            ArgType.String,
            shortName = KEY_FILE.shortName,
            fullName = KEY_FILE.fullName,
            description = KEY_FILE.description
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

        val isXmlKeyFile by parser.option(
            ArgType.Boolean,
            shortName = XML_KEY.shortName,
            fullName = XML_KEY.fullName,
            description = XML_KEY.description
        )

        parser.parse(args)

        return RawArgs(
            filePath ?: EMPTY,
            keyPath,
            delayInSeconds ?: EMPTY,
            inputReader ?: EMPTY,
            isXmlKeyFile ?: false
        )
    }
}