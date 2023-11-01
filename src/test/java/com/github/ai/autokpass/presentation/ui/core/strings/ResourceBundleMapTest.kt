package com.github.ai.autokpass.presentation.ui.core.strings

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.ResourceBundle
import org.junit.jupiter.api.Test

class ResourceBundleMapTest {

    private val resources = mockk<ResourceBundle>()

    @Test
    fun `get should call wrapped ResourceBundle`() {
        every { resources.getString(KEY) } returns VALUE

        newResourceMap()[KEY] shouldBe VALUE
    }

    @Test
    fun `other methods should throws exception`() {
        shouldThrow<NotImplementedError> { newResourceMap().entries }
        shouldThrow<NotImplementedError> { newResourceMap().keys }
        shouldThrow<NotImplementedError> { newResourceMap().size }
        shouldThrow<NotImplementedError> { newResourceMap().values }
        shouldThrow<NotImplementedError> { newResourceMap().containsKey(KEY) }
        shouldThrow<NotImplementedError> { newResourceMap().containsValue(VALUE) }
        shouldThrow<NotImplementedError> { newResourceMap().isEmpty() }
    }

    private fun newResourceMap(): ResourceBundleMap =
        ResourceBundleMap(resources)

    companion object {
        private const val KEY = "key"
        private const val VALUE = "value"
    }
}