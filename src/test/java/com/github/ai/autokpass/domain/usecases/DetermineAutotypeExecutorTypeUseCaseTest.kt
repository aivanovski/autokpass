package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.Errors.FAILED_TO_DETERMINE_AUTOTYPE_EXECUTOR_TYPE
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.Result
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DetermineAutotypeExecutorTypeUseCaseTest {

    private val useCase = DetermineAutotypeExecutorTypeUseCase()

    @Test
    fun `getAutotypeExecutorType should return type for linux`() {
        assertThat(useCase.getAutotypeExecutorType(OSType.LINUX, null).getDataOrNull())
            .isEqualTo(AutotypeExecutorType.XDOTOOL)
    }

    @Test
    fun `getAutotypeExecutorType should return type for mac os`() {
        assertThat(useCase.getAutotypeExecutorType(OSType.MAC_OS, null).getDataOrNull())
            .isEqualTo(AutotypeExecutorType.CLICLICK)
    }

    @Test
    fun `getAutotypeExecutorType should from arguments`() {
        assertThat(useCase.getAutotypeExecutorType(null, AutotypeExecutorType.XDOTOOL).getDataOrNull())
            .isEqualTo(AutotypeExecutorType.XDOTOOL)

        assertThat(useCase.getAutotypeExecutorType(null, AutotypeExecutorType.CLICLICK).getDataOrNull())
            .isEqualTo(AutotypeExecutorType.CLICLICK)
    }

    @Test
    fun `getAutotypeExecutorType should return error`() {
        val result = useCase.getAutotypeExecutorType(null, null)

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat(result.getErrorOrThrow().exception).isInstanceOf(AutokpassException::class.java)
        assertThat(result.getErrorOrThrow().exception.message).isEqualTo(FAILED_TO_DETERMINE_AUTOTYPE_EXECUTOR_TYPE)
    }
}
