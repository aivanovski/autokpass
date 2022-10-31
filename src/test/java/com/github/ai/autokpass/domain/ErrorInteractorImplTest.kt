package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.printer.Printer
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test

class ErrorInteractorImplTest {

    private val printer = mockk<Printer>()

    @Test
    fun `process should print stack trace`() {
        // arrange
        val exception = mockk<Exception>()
        every { printer.println(any()) }.returns(Unit)
        every { exception.printStackTrace() }.returns(Unit)
        every { exception.message }.returns(MESSAGE)

        // act
        ErrorInteractorImpl(printer)
            .process(Result.Error(exception))

        // assert
        verifySequence {
            exception.printStackTrace()
            exception.message
            printer.println(MESSAGE)
        }
    }

    @Test
    fun `process should not print stack trace`() {
        // arrange
        val exception = mockk<AutokpassException>()
        every { printer.println(any()) }.returns(Unit)
        every { exception.message }.returns(MESSAGE)

        // act
        ErrorInteractorImpl(printer)
            .process(Result.Error(exception))

        // assert
        verifySequence {
            exception.message
            printer.println(MESSAGE)
        }
    }

    @Test
    fun `process should print Exception message`() {
        // arrange
        val exception = mockk<AutokpassException>()
        every { printer.println(MESSAGE) }.returns(Unit)
        every { exception.message }.returns(MESSAGE)

        // act
        ErrorInteractorImpl(printer)
            .process(Result.Error(exception))

        // assert
        verifySequence {
            exception.message
            printer.println(MESSAGE)
        }
    }

    @Test
    fun `process should call toString on Exception`() {
        // arrange
        val exception = mockk<AutokpassException>()
        every { printer.println(EXCEPTION_STRING) }.returns(Unit)
        every { exception.message }.returns(null)
        every { exception.toString() }.returns(EXCEPTION_STRING)

        // act
        ErrorInteractorImpl(printer)
            .process(Result.Error(exception))

        // assert
        verifySequence {
            exception.message
            exception.toString()
            printer.println(EXCEPTION_STRING)
        }
    }

    @Test
    fun `processFailed should return false if result is success`() {
        // act
        val result = ErrorInteractorImpl(printer)
            .processFailed(Result.Success(null))

        // assert
        assertThat(result).isFalse()
    }

    @Test
    fun `processFailed should return true and process Exception`() {
        // arrange
        val exception = mockk<AutokpassException>()
        every { printer.println(MESSAGE) }.returns(Unit)
        every { exception.message }.returns(MESSAGE)

        // act
        val result = ErrorInteractorImpl(printer)
            .processFailed(Result.Error(exception))

        // assert
        verifySequence {
            exception.message
            printer.println(MESSAGE)
        }
        assertThat(result).isTrue()
    }

    companion object {
        private const val MESSAGE = "message"
        private const val EXCEPTION_STRING = "exceptionString"
    }
}