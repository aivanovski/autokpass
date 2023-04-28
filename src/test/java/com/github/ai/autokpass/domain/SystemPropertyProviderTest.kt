package com.github.ai.autokpass.domain

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SystemPropertyProviderTest {

    @Test
    fun `getSystemProperty should return value`() {
        // arrange
        System.setProperty(CUSTOM_PROPERTY_NAME, CUSTOM_PROPERTY_VALUE)

        // act
        val result = SystemPropertyProvider().getSystemProperty(CUSTOM_PROPERTY_NAME)

        // assert
        result shouldBe CUSTOM_PROPERTY_VALUE
    }

    @Test
    fun `getSystemProperty should return empty value`() {
        // arrange
        System.getProperty(NON_EXIST_PROPERTY_NAME).should(beNull())

        // act
        val result = SystemPropertyProvider().getSystemProperty(NON_EXIST_PROPERTY_NAME)

        // assert
        result.length shouldBe 0
    }

    companion object {
        private const val CUSTOM_PROPERTY_NAME = "custom_property_name"
        private const val CUSTOM_PROPERTY_VALUE = "custom_property_value"
        private const val NON_EXIST_PROPERTY_NAME = "non_exist_property_name"
    }
}