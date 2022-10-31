package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase.Companion.GREETINGS_TEMPLATE
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase.Companion.PROPERTIES_FILE_NAME
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.utils.resourceAsString
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class PrintGreetingsUseCaseTest {

    @Test
    fun `printGreetings should print message with version`() {
        // arrange
        val message = String.format(GREETINGS_TEMPLATE, readVersionFromProperties())
        val printer = mockk<Printer>()

        every { printer.println(message) }.returns(Unit)

        // act
        PrintGreetingsUseCase(printer).printGreetings()

        // assert
        verify { printer.println(message) }
    }

    private fun readVersionFromProperties(): String {
        val values = resourceAsString(PROPERTIES_FILE_NAME).split("=")
        return values[1]
    }
}