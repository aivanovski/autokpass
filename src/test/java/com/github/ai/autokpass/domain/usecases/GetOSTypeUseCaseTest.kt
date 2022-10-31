package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.Errors
import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase.Companion.PROPERTY_OS_NAME
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class GetOSTypeUseCaseTest {

    @Test
    fun `getOSType should determine linux`() {
        // arrange
        val propertyProvider = mockk<SystemPropertyProvider>()
        every { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }.returns("Linux")

        // act
        val result = GetOSTypeUseCase(propertyProvider).getOSType()

        // assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.getDataOrThrow()).isEqualTo(OSType.LINUX)

        verify { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }
    }

    @Test
    fun `getOSType should determine mac os`() {
        // arrange
        val propertyProvider = mockk<SystemPropertyProvider>()
        every { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }.returns("Mac OS X")

        // act
        val result = GetOSTypeUseCase(propertyProvider).getOSType()

        // assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.getDataOrThrow()).isEqualTo(OSType.MAC_OS)

        verify { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }
    }

    @Test
    fun `getOSType should return error`() {
        // arrange
        val propertyProvider = mockk<SystemPropertyProvider>()
        every { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }.returns("Windows")

        // act
        val result = GetOSTypeUseCase(propertyProvider).getOSType()

        // assert
        assertThat(result).isInstanceOf(Result.Error::class.java)

        val exception = result.asErrorOrThrow().exception
        assertThat(exception).isInstanceOf(AutokpassException::class.java)
        assertThat(exception.message).isEqualTo(Errors.FAILED_TO_DETERMINE_OS_TYPE)

        verify { propertyProvider.getSystemProperty(PROPERTY_OS_NAME) }
    }
}