package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.model.Result
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class ErrorInteractor(
    private val printer: Printer
) {

    fun processAndExit(error: Result.Error): Nothing {
        if (error.exception !is AutokpassException) {
            error.exception.printStackTrace()
        }

        printer.println(error.exception.message ?: error.exception.toString())
        Thread.sleep(DELAY_BEFORE_EXIT)
        exitProcess(1)
    }

    fun exit(message: String): Nothing {
        printer.println(message)
        Thread.sleep(DELAY_BEFORE_EXIT)
        exitProcess(1)
    }

    fun exit(): Nothing {
        exitProcess(0)
    }

    companion object {
        private val DELAY_BEFORE_EXIT = TimeUnit.SECONDS.toMillis(2)
    }
}