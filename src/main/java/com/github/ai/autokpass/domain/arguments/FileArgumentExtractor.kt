package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class FileArgumentExtractor(
    private val strings: StringResources,
    private val content: InputStream
) : ArgumentExtractor {

    override fun extractArguments(): Result<RawArgs> {
        return try {
            val reader = InputStreamReader(content)

            val argsMap = mutableMapOf<String, String>()
            for (line in reader.readLines()) {
                if (line.startsWith("#")) {
                    continue
                }

                val keyAndValue = line.split("=")
                if (keyAndValue.size != 2) {
                    return Result.Error(ParsingException(strings.errorFailedToParseConfigFile))
                }

                argsMap[keyAndValue[0]] = keyAndValue[1]
            }

            val args = RawArgs(
                filePath = argsMap[Argument.FILE.fullName],
                keyPath = argsMap[Argument.KEY_FILE.fullName],
                startDelay = argsMap[Argument.DELAY.fullName],
                delayBetweenActions = argsMap[Argument.AUTOTYPE_DELAY.fullName],
                inputType = argsMap[Argument.INPUT.fullName],
                autotypeType = argsMap[Argument.AUTOTYPE.fullName],
                keyProcessingCommand = argsMap[Argument.PROCESS_KEY_COMMAND.fullName]
            )

            Result.Success(args)
        } catch (exception: IOException) {
            Result.Error(exception)
        }
    }
}