package com.github.ai.autokpass.domain

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class SystemPropertyProviderTest {

    @Test
    fun `getSystemProperty should return value`() {
        // arrange
        System.setProperty(CUSTOM_PROPERTY_NAME, CUSTOM_PROPERTY_VALUE)

        // act
        val result = SystemPropertyProvider().getSystemProperty(CUSTOM_PROPERTY_NAME)

        // assert
        assertThat(result).isEqualTo(CUSTOM_PROPERTY_VALUE)
    }

    @Test
    fun `getSystemProperty should return empty value`() {
        // arrange
        assertThat(System.getProperty(NON_EXIST_PROPERTY_NAME)).isNull()

        // act
        val result = SystemPropertyProvider().getSystemProperty(NON_EXIST_PROPERTY_NAME)

        // assert
        assertThat(result).isEmpty()
    }

    companion object {
        private const val CUSTOM_PROPERTY_NAME = "custom_property_name"
        private const val CUSTOM_PROPERTY_VALUE = "custom_property_value"
        private const val NON_EXIST_PROPERTY_NAME = "non_exist_property_name"
    }
}