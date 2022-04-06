package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.presentation.printer.Printer
import java.util.Properties

class PrintGreetingsUseCase(
    private val printer: Printer
) {

    fun printGreetings() {
        val properties = Properties()
        properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME))

        val version = properties[PROPERTY_VERSION]
        printer.println(String.format(GREETINGS_TEMPLATE, version))
    }

    companion object {
        private const val GREETINGS_TEMPLATE = "Autokpass v%s"
        private const val PROPERTIES_FILE_NAME = "version.properties"
        private const val PROPERTY_VERSION = "version"
    }
}