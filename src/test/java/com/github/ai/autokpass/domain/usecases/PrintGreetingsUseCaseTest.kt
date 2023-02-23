package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase.Companion.PROPERTIES_FILE_NAME
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import com.github.ai.autokpass.utils.resourceAsString
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class PrintGreetingsUseCaseTest {

    private val strings: StringResources = StringResourcesImpl()

    @Test
    fun `printGreetings should print message with version`() {
        // arrange
        val message = String.format(strings.greetingsMessage, readVersionFromProperties())
        val printer = mockk<Printer>()

        every { printer.println(message) }.returns(Unit)

        // act
        PrintGreetingsUseCase(printer, strings).printGreetings()

        // assert
        verify { printer.println(message) }
    }

    private fun readVersionFromProperties(): String {
        val values = resourceAsString(PROPERTIES_FILE_NAME).split("=")
        return values[1]
    }
}