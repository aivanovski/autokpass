package com.github.ai.autokpass.data.config

import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.util.StringUtils.NEW_LINE
import java.lang.StringBuilder

class FileMarshaller(
    private val strings: StringResources
) {

    fun marshall(values: Map<String, String>): String {
        val content = StringBuilder()

        for ((key, value) in values.entries) {
            content.append("$key=$value$NEW_LINE")
        }

        return content.toString()
    }

    fun unmarshall(content: String): Result<Map<String, String>> {
        val values = mutableMapOf<String, String>()

        for (line in content.split(NEW_LINE)) {
            if (line.startsWith("#") || line.isBlank()) {
                continue
            }

            val keyAndValue = line.split("=")
            if (keyAndValue.size != 2) {
                return Result.Error(ParsingException(strings.errorFailedToParseConfigFile))
            }

            values[keyAndValue[0]] = keyAndValue[1]
        }

        return Result.Success(values)
    }
}