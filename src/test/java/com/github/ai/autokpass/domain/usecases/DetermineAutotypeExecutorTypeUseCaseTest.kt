package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.Errors.FAILED_TO_DETERMINE_AUTOTYPE_EXECUTOR_TYPE
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.model.AutotypeExecutorType.CLICLICK
import com.github.ai.autokpass.model.AutotypeExecutorType.XDOTOOL
import com.github.ai.autokpass.model.OSType
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import org.junit.jupiter.api.Test

class DetermineAutotypeExecutorTypeUseCaseTest {

    private val useCase = DetermineAutotypeExecutorTypeUseCase()

    @Test
    fun `getAutotypeExecutorType should return type for linux`() {
        useCase.getAutotypeExecutorType(OSType.LINUX, null).getDataOrNull()
            .shouldBe(XDOTOOL)
    }

    @Test
    fun `getAutotypeExecutorType should return type for mac os`() {
        useCase.getAutotypeExecutorType(OSType.MAC_OS, null).getDataOrNull()
            .shouldBe(CLICLICK)
    }

    @Test
    fun `getAutotypeExecutorType should from arguments`() {
        useCase.getAutotypeExecutorType(null, XDOTOOL).getDataOrNull()
            .shouldBe(XDOTOOL)

        useCase.getAutotypeExecutorType(null, CLICLICK).getDataOrNull()
            .shouldBe(CLICLICK)
    }

    @Test
    fun `getAutotypeExecutorType should return error`() {
        val result = useCase.getAutotypeExecutorType(null, null)

        with(result) {
            isFailed() shouldBe true
            getExceptionOrThrow() should beInstanceOf<AutokpassException>()
            getExceptionOrThrow().message shouldBe FAILED_TO_DETERMINE_AUTOTYPE_EXECUTOR_TYPE
        }
    }
}
