package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import java.util.Properties

class PrintGreetingsUseCase(
    private val printer: Printer,
    private val strings: StringResources
) {

    fun printGreetings() {
        val properties = Properties()
        properties.load(PrintGreetingsUseCase::class.java.classLoader.getResourceAsStream(PROPERTIES_FILE_NAME))

        val version = properties[PROPERTY_VERSION]
        printer.println(String.format(strings.greetingsMessage, version))
    }

    companion object {
        const val PROPERTIES_FILE_NAME = "version.properties"
        const val PROPERTY_VERSION = "version"
    }
}