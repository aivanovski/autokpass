package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase.Companion.PROPERTY_OS_NAME
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class GetOSTypeUseCaseTest {

    private val strings: StringResources = StringResourcesImpl()

    @Test
    fun `getOSType should determine linux`() {
        // arrange
        val propertyProvider = mockk<SystemPropertyProvider>()
        every { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }.returns("Linux")

        // act
        val result = GetOSTypeUseCase(propertyProvider, strings).getOSType()

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe OSType.LINUX

        verify { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }
    }

    @Test
    fun `getOSType should determine mac os`() {
        // arrange
        val propertyProvider = mockk<SystemPropertyProvider>()
        every { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }.returns("Mac OS X")

        // act
        val result = GetOSTypeUseCase(propertyProvider, strings).getOSType()

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe OSType.MAC_OS

        verify { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }
    }

    @Test
    fun `getOSType should return error`() {
        // arrange
        val propertyProvider = mockk<SystemPropertyProvider>()
        every { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }.returns("Windows")

        // act
        val result = GetOSTypeUseCase(propertyProvider, strings).getOSType()

        // assert
        with(result) {
            isFailed() shouldBe true
            getExceptionOrThrow() should beInstanceOf<AutokpassException>()
            getExceptionOrThrow().message shouldBe strings.errorFailedToDetermineOsType
        }

        verify { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }
    }
}