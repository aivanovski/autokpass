package com.github.ai.autokpass.presentation.ui.core.strings

import com.github.ai.autokpass.utils.resourceAsString
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.lang.reflect.Method

internal class StringResourcesImplTest {

    @Test
    fun `strings should be correct`() {
        val strings = StringResourcesImpl()
        val parsedResourcesMap = parseStringResources()

        val type = StringResources::class.java
        for (method in type.declaredMethods) {
            val resourceKey = formatResourceName(method)
            val resourceValue = method.invoke(strings)

            resourceValue shouldBe parsedResourcesMap[resourceKey]
        }
    }

    private fun formatResourceName(method: Method): String {
        val name = method.name.removePrefix("get")
        val firstLetter = name.first()

        return name.replaceFirst(firstLetter, firstLetter.lowercaseChar(), ignoreCase = false)
    }

    private fun parseStringResources(): Map<String, String> {
        return resourceAsString("strings.properties")
            .split("\n")
            .mapNotNull { line -> parseStringResourceLine(line) }
            .toMap()
    }

    private fun parseStringResourceLine(line: String): Pair<String, String>? {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) {
            return null
        }

        val splitIdx = trimmed.indexOf("=")
        if (splitIdx == -1) {
            return null
        }

        val key = line.substring(0, splitIdx)
        val value = line.substring(splitIdx + 1, line.length)

        return if (key.isNotBlank() && value.isNotBlank()) {
            Pair(key, value)
        } else {
            null
        }
    }
}