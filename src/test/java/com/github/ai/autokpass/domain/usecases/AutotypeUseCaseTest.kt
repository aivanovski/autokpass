package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.TestData.ENTRY1
import com.github.ai.autokpass.domain.Errors.FAILED_TO_COMPILE_AUTOTYPE_SEQUENCE
import com.github.ai.autokpass.domain.autotype.AutotypeExecutor
import com.github.ai.autokpass.domain.autotype.AutotypeExecutorProvider
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.autotype.ThreadThrottler
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.usecases.AutotypeUseCase.Companion.DELAY_MESSAGE
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.AutotypePattern.Companion.DEFAULT_PATTERN
import com.github.ai.autokpass.presentation.printer.Printer
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Test

class AutotypeUseCaseTest {

    private val sequenceFactory = mockk<AutotypeSequenceFactory>()
    private val printer = mockk<Printer>()
    private val throttler = mockk<ThreadThrottler>()
    private val executor = mockk<AutotypeExecutor>()
    private val executorProvider = mockk<AutotypeExecutorProvider>()

    @Test
    fun `doAutotype should create sequence and pass to executor`() {
        // arrange
        every { sequenceFactory.createAutotypeSequence(ENTRY1, DEFAULT_PATTERN) }.returns(SEQUENCE)
        every { executor.execute(SEQUENCE) }.returns(Unit)
        every { executorProvider.getExecutor(AutotypeExecutorType.XDOTOOL) }.returns(executor)

        // act
        val result = createUseCase()
            .doAutotype(
                executorType = AutotypeExecutorType.XDOTOOL,
                entry = ENTRY1,
                pattern = DEFAULT_PATTERN,
                delayInSeconds = null
            )

        // assert
        verifySequence {
            sequenceFactory.createAutotypeSequence(ENTRY1, DEFAULT_PATTERN)
            executorProvider.getExecutor(AutotypeExecutorType.XDOTOOL)
            executor.execute(SEQUENCE)
        }

        assertThat(result.isSucceeded()).isTrue()
    }

    @Test
    fun `doAutotype should return error if sequence wasn't created`() {
        // arrange
        every { sequenceFactory.createAutotypeSequence(ENTRY1, DEFAULT_PATTERN) }.returns(null)

        // act
        val result = createUseCase()
            .doAutotype(
                executorType = AutotypeExecutorType.XDOTOOL,
                entry = ENTRY1,
                pattern = DEFAULT_PATTERN,
                delayInSeconds = null
            )

        // assert
        every { sequenceFactory.createAutotypeSequence(ENTRY1, DEFAULT_PATTERN) }

        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(AutokpassException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(FAILED_TO_COMPILE_AUTOTYPE_SEQUENCE)
    }

    @Test
    fun `doAutotype should make delay if it was specified`() {
        // arrange
        val message = String.format(DELAY_MESSAGE, DELAY_IN_SECONDS)
        every { sequenceFactory.createAutotypeSequence(ENTRY1, DEFAULT_PATTERN) }.returns(SEQUENCE)
        every { executor.execute(SEQUENCE) }.returns(Unit)
        every { printer.println(message) }.returns(Unit)
        every { throttler.sleep(DELAY_IN_MILLIS) }.returns(Unit)
        every { executorProvider.getExecutor(AutotypeExecutorType.XDOTOOL) }.returns(executor)

        // act
        val result = createUseCase()
            .doAutotype(
                executorType = AutotypeExecutorType.XDOTOOL,
                entry = ENTRY1,
                pattern = DEFAULT_PATTERN,
                delayInSeconds = DELAY_IN_SECONDS
            )

        // assert
        verifySequence {
            sequenceFactory.createAutotypeSequence(ENTRY1, DEFAULT_PATTERN)
            printer.println(message)
            throttler.sleep(DELAY_IN_MILLIS)
            executorProvider.getExecutor(AutotypeExecutorType.XDOTOOL)
            executor.execute(SEQUENCE)
        }

        assertThat(result.isSucceeded()).isTrue()
    }

    private fun createUseCase(): AutotypeUseCase {
        return AutotypeUseCase(
            executorProvider = executorProvider,
            sequenceFactory = sequenceFactory,
            throttler = throttler,
            printer = printer
        )
    }

    companion object {
        private val SEQUENCE = AutotypeSequenceFactory().createAutotypeSequence(ENTRY1, DEFAULT_PATTERN)!!
        private const val DELAY_IN_SECONDS = 3L
        private const val DELAY_IN_MILLIS = 3000L
    }
}