package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.TestData.EXCEPTION
import com.github.ai.autokpass.domain.usecases.DetermineDesktopUseCase.Companion.COMMAND
import com.github.ai.autokpass.domain.usecases.DetermineDesktopUseCase.Companion.DESKTOP_SESSION
import com.github.ai.autokpass.domain.usecases.DetermineDesktopUseCase.Companion.XDG_SESSION_TYPE
import com.github.ai.autokpass.model.DesktopType
import com.github.ai.autokpass.model.DesktopType.WAYLAND
import com.github.ai.autokpass.model.DesktopType.XORG
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.process.MockProcessExecutor
import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DetermineDesktopUseCaseTest {

    @Test
    fun `should return error if unable to execute env command`() {
        // arrange
        val processExecutor = MockProcessExecutor(
            data = mapOf(
                COMMAND to Result.Error(EXCEPTION)
            )
        )

        // act
        val result = DetermineDesktopUseCase(processExecutor).getDesktopType()

        // assert
        result shouldBe Result.Error(EXCEPTION)
    }

    @Test
    fun `should classify by environment variables`() {
        assertUseCase(
            data = listOf(
                "$XDG_SESSION_TYPE=wayland-session" to Result.Success(WAYLAND),
                "$XDG_SESSION_TYPE=x11-session" to Result.Success(XORG),
                "$XDG_SESSION_TYPE=xorg-session" to Result.Success(XORG),

                "$DESKTOP_SESSION=wayland-session" to Result.Success(WAYLAND),
                "$DESKTOP_SESSION=x11-session-x11" to Result.Success(XORG),
                "$DESKTOP_SESSION=xorg-session" to Result.Success(XORG),

                "ENVIRONMENT_VARIABLE=wayland-session" to Result.Success(WAYLAND)
            )
        )
    }

    @Test
    fun `should return xorg if unable to classify by environment variables`() {
        assertUseCase(
            data = listOf(
                "$XDG_SESSION_TYPE=invalid" to Result.Success(XORG),
                "$DESKTOP_SESSION=invalid" to Result.Success(XORG)
            )
        )
    }

    @Test
    fun `should return xorg type if nothing is specified`() {
        assertUseCase(
            data = listOf(EMPTY to Result.Success(XORG))
        )
    }

    @Test
    fun `should ignore invalid lines`() {
        assertUseCase(
            data = listOf(
                """
                $XDG_SESSION_TYPE=x11=invalid
                $DESKTOP_SESSION=wayland
                """.trimIndent() to Result.Success(WAYLAND),

                """
                $XDG_SESSION_TYPE=wayland=invalid
                $DESKTOP_SESSION=x11
                """.trimIndent() to Result.Success(XORG),

                """
                WAYLAND
                $DESKTOP_SESSION=x11
                """.trimIndent() to Result.Success(XORG)
            )
        )
    }

    private fun assertUseCase(
        data: List<Pair<String, Result<DesktopType>>>
    ) {
        data.forEach { (input, expected) ->
            val processExecutor = MockProcessExecutor(
                data = mapOf(
                    COMMAND to Result.Success(input)
                )
            )

            // act
            val result = DetermineDesktopUseCase(
                processExecutor = processExecutor
            ).getDesktopType()

            // assert
            result shouldBe expected
        }
    }
}