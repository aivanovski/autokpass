package com.github.ai.autokpass.domain

import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.slf4j.Logger

class ErrorInteractorImplTest {

    private val logger = mockk<Logger>()
    private val strings: StringResources = StringResourcesImpl()

    @Test
    fun `process should print Exception message`() {
        // arrange
        val exception = Exception(EXCEPTION_MESSAGE)
        val message = formatExceptionForLogger(exception)
        every { logger.error(message, exception) }.returns(Unit)

        // act
        ErrorInteractorImpl(logger, strings).process(Result.Error(exception))

        // assert
        verify { logger.error(message, exception) }
    }

    @Test
    fun `process should print default message if Exception message is empty`() {
        // arrange
        val exception = Exception(EMPTY)
        val message = formatExceptionForLogger(exception)
        every { logger.error(message, exception) }.returns(Unit)

        // act
        ErrorInteractorImpl(logger, strings).process(Result.Error(exception))

        // assert
        verify { logger.error(message, exception) }
    }

    @Test
    fun `processFailed should return false if result is success`() {
        // act
        val result = ErrorInteractorImpl(logger, strings).processFailed(Result.Success(null))

        // assert
        result shouldBe false
    }

    @Test
    fun `processFailed should return true and process Exception`() {
        // arrange
        val exception = Exception(EXCEPTION_MESSAGE)
        val message = formatExceptionForLogger(exception)
        every { logger.error(message, exception) }.returns(Unit)

        // act
        val result = ErrorInteractorImpl(logger, strings)
            .processFailed(Result.Error(exception))

        // assert
        verify { logger.error(message, exception) }
        result shouldBe true
    }

    @Test
    fun `processAndGetMessage should process error and return message`() {
        // arrange
        val exception = Exception(EXCEPTION_MESSAGE)
        val message = formatExceptionForLogger(exception)
        every { logger.error(message, exception) }.returns(Unit)

        // act
        val result = ErrorInteractorImpl(logger, strings)
            .processAndGetMessage(Result.Error(exception))

        // assert
        verify { logger.error(message, exception) }
        result shouldBe EXCEPTION_MESSAGE
    }

    @Test
    fun `processAndGetMessage should process error and convert Exception to String`() {
        // arrange
        val exception = Exception(EMPTY)
        val message = formatExceptionForLogger(exception)
        every { logger.error(message, exception) }.returns(Unit)

        // act
        val result = ErrorInteractorImpl(logger, strings)
            .processAndGetMessage(Result.Error(exception))

        // assert
        verify { logger.error(message, exception) }
        result shouldBe exception.toString()
    }

    private fun formatExceptionForLogger(exception: Exception): String {
        return if (exception.message.isNullOrEmpty()) {
            strings.errorHasBeenOccurred
        } else {
            "${strings.errorHasBeenOccurred}: ${exception.message}"
        }
    }

    companion object {
        private const val EXCEPTION_MESSAGE = "Test exception message"
    }
}