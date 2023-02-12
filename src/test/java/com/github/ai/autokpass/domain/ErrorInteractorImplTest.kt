package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.Errors.ERROR_HAS_BEEN_OCCURRED
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.slf4j.Logger

class ErrorInteractorImplTest {

    private val logger = mockk<Logger>()

    @Test
    fun `process should print Exception message`() {
        // arrange
        val exception = Exception(EXCEPTION_MESSAGE)
        every { logger.error(LOGGER_MESSAGE, exception) }.returns(Unit)

        // act
        ErrorInteractorImpl(logger).process(Result.Error(exception))

        // assert
        verify { logger.error(LOGGER_MESSAGE, exception) }
    }

    @Test
    fun `process should print default message if Exception message is empty`() {
        // arrange
        val exception = Exception(EMPTY)
        every { logger.error(LOGGER_MESSAGE_EMPTY_EXCEPTION_MESSAGE, exception) }.returns(Unit)

        // act
        ErrorInteractorImpl(logger).process(Result.Error(exception))

        // assert
        verify { logger.error(LOGGER_MESSAGE_EMPTY_EXCEPTION_MESSAGE, exception) }
    }

    @Test
    fun `processFailed should return false if result is success`() {
        // act
        val result = ErrorInteractorImpl(logger).processFailed(Result.Success(null))

        // assert
        result shouldBe false
    }

    @Test
    fun `processFailed should return true and process Exception`() {
        // arrange
        val exception = Exception(EXCEPTION_MESSAGE)
        every { logger.error(LOGGER_MESSAGE, exception) }.returns(Unit)

        // act
        val result = ErrorInteractorImpl(logger)
            .processFailed(Result.Error(exception))

        // assert
        verify { logger.error(LOGGER_MESSAGE, exception) }
        result shouldBe true
    }

    @Test
    fun `processAndGetMessage should process error and return message`() {
        // arrange
        val exception = Exception(EXCEPTION_MESSAGE)
        every { logger.error(LOGGER_MESSAGE, exception) }.returns(Unit)

        // act
        val result = ErrorInteractorImpl(logger)
            .processAndGetMessage(Result.Error(exception))

        // assert
        verify { logger.error(LOGGER_MESSAGE, exception) }
        result shouldBe EXCEPTION_MESSAGE
    }

    @Test
    fun `processAndGetMessage should process error and convert Exception to String`() {
        // arrange
        val exception = Exception(EMPTY)
        every { logger.error(LOGGER_MESSAGE_EMPTY_EXCEPTION_MESSAGE, exception) }.returns(Unit)

        // act
        val result = ErrorInteractorImpl(logger)
            .processAndGetMessage(Result.Error(exception))

        // assert
        verify { logger.error(LOGGER_MESSAGE_EMPTY_EXCEPTION_MESSAGE, exception) }
        result shouldBe exception.toString()
    }

    companion object {
        private const val EXCEPTION_MESSAGE = "message"
        private const val LOGGER_MESSAGE = "$ERROR_HAS_BEEN_OCCURRED: $EXCEPTION_MESSAGE"
        private const val LOGGER_MESSAGE_EMPTY_EXCEPTION_MESSAGE = ERROR_HAS_BEEN_OCCURRED
    }
}