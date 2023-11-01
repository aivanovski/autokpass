package com.github.ai.autokpass.presentation.ui.core.strings

import com.github.ai.autokpass.utils.resourceAsStream
import io.kotest.matchers.shouldBe
import java.lang.reflect.Method
import java.util.Properties
import org.junit.jupiter.api.Test

internal class StringResourcesImplTest {

    @Test
    fun `all strings should be correct`() {
        // arrange
        val strings = StringResourcesImpl()
        val expectedResources = loadStringResources()

        // act
        val actualResources = StringResources::class.java.declaredMethods
            .associate { method ->
                val resourceKey = formatResourceName(method)
                resourceKey to method.invoke(strings)
            }

        // assert
        actualResources shouldBe expectedResources
    }

    private fun formatResourceName(method: Method): String {
        val name = method.name.removePrefix("get")
        val firstLetter = name.first()

        return name.replaceFirst(firstLetter, firstLetter.lowercaseChar(), ignoreCase = false)
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadStringResources(): Map<String, String> {
        return Properties()
            .apply {
                load(resourceAsStream("strings.properties"))
            }
            .toMap() as Map<String, String>
    }
}