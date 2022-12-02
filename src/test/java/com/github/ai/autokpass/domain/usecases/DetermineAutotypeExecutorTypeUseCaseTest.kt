package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.TestData.EXCEPTION
import com.github.ai.autokpass.model.AutotypeExecutorType.CLICLICK
import com.github.ai.autokpass.model.AutotypeExecutorType.OSA_SCRIPT
import com.github.ai.autokpass.model.AutotypeExecutorType.XDOTOOL
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.Result
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beTheSameInstanceAs
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class DetermineAutotypeExecutorTypeUseCaseTest {

    private val getOsTypeUseCase = mockk<GetOSTypeUseCase>()
    private val useCase = DetermineAutotypeExecutorTypeUseCase(getOsTypeUseCase)

    @Test
    fun `getAutotypeExecutorType should return type for linux`() {
        // arrange
        every { getOsTypeUseCase.getOSType() }.returns(Result.Success(OSType.LINUX))

        // act
        val result = useCase.getAutotypeExecutorType(autotypeFromArgs = null)


        // assert
        verify { getOsTypeUseCase.getOSType() }
        result shouldBe Result.Success(XDOTOOL)
    }

    @Test
    fun `getAutotypeExecutorType should return type for mac os`() {
        // arrange
        every { getOsTypeUseCase.getOSType() }.returns(Result.Success(OSType.MAC_OS))

        // act
        val result = useCase.getAutotypeExecutorType(autotypeFromArgs = null)

        // assert
        verify { getOsTypeUseCase.getOSType() }
        result shouldBe Result.Success(OSA_SCRIPT)
    }

    @Test
    fun `getAutotypeExecutorType should from arguments`() {
        // arrange
        val types = listOf(XDOTOOL, OSA_SCRIPT, CLICLICK)
        every { getOsTypeUseCase.getOSType() }.returns(Result.Success(OSType.LINUX))

        // assert
        for (type in types) {
            val result = useCase.getAutotypeExecutorType(autotypeFromArgs = type)
            result shouldBe Result.Success(type)
        }
    }

    @Test
    fun `getAutotypeExecutorType should return error`() {
        // arrange
        every { getOsTypeUseCase.getOSType() }.returns(Result.Error(EXCEPTION))

        // act
        val result = useCase.getAutotypeExecutorType(autotypeFromArgs = null)

        // assert
        verify { getOsTypeUseCase.getOSType() }
        with(result) {
            isFailed() shouldBe true
            getExceptionOrThrow() should beTheSameInstanceAs(EXCEPTION)
        }
    }
}
