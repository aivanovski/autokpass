package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.model.RawConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class FileConfigReader(
    private val strings: StringResources,
    private val content: InputStream
) : ConfigReader {

    override fun readConfig(): Result<RawConfig> {
        return try {
            val reader = InputStreamReader(content)

            val values = mutableMapOf<String, String>()
            for (line in reader.readLines()) {
                if (line.startsWith("#")) {
                    continue
                }

                val keyAndValue = line.split("=")
                if (keyAndValue.size != 2) {
                    return Result.Error(ParsingException(strings.errorFailedToParseConfigFile))
                }

                values[keyAndValue[0]] = keyAndValue[1]
            }

            val config = RawConfig(
                filePath = values[Argument.FILE.fullName],
                keyPath = values[Argument.KEY_FILE.fullName],
                startDelay = values[Argument.DELAY.fullName],
                delayBetweenActions = values[Argument.AUTOTYPE_DELAY.fullName],
                autotypeType = values[Argument.AUTOTYPE.fullName],
                keyProcessingCommand = values[Argument.PROCESS_KEY_COMMAND.fullName]
            )

            Result.Success(config)
        } catch (exception: IOException) {
            Result.Error(exception)
        }
    }
}