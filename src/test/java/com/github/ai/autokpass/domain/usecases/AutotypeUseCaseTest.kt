package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.TestData.ENTRY1
import com.github.ai.autokpass.domain.Errors.FAILED_TO_COMPILE_AUTOTYPE_SEQUENCE
import com.github.ai.autokpass.domain.autotype.AutotypeExecutor
import com.github.ai.autokpass.domain.autotype.AutotypeExecutorFactory
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.autotype.ThreadThrottler
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.usecases.AutotypeUseCase.Companion.DELAY_MESSAGE
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.AutotypePattern.Companion.DEFAULT_PATTERN
import com.github.ai.autokpass.presentation.printer.Printer
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test

class AutotypeUseCaseTest {

    private val sequenceFactory = mockk<AutotypeSequenceFactory>()
    private val printer = mockk<Printer>()
    private val throttler = mockk<ThreadThrottler>()
    private val executor = mockk<AutotypeExecutor>()
    private val autotypeExecutorFactory = mockk<AutotypeExecutorFactory>()

    @Test
    fun `doAutotype should create sequence and pass to executor`() {
        // arrange
        every {
            sequenceFactory.createAutotypeSequence(
                ENTRY1,
                DEFAULT_PATTERN,
                DELAY_BETWEEN_ACTIONS_IN_MILLIS
            )
        }.returns(SEQUENCE)
        every { executor.execute(SEQUENCE) }.returns(Unit)
        every { autotypeExecutorFactory.getExecutor(AutotypeExecutorType.XDOTOOL) }.returns(executor)

        // act
        val result = createUseCase()
            .doAutotype(
                executorType = AutotypeExecutorType.XDOTOOL,
                entry = ENTRY1,
                pattern = DEFAULT_PATTERN,
                delayBetweenActions = DELAY_BETWEEN_ACTIONS_IN_MILLIS,
                startDelayInSeconds = null
            )

        // assert
        verifySequence {
            sequenceFactory.createAutotypeSequence(ENTRY1, DEFAULT_PATTERN, DELAY_BETWEEN_ACTIONS_IN_MILLIS)
            autotypeExecutorFactory.getExecutor(AutotypeExecutorType.XDOTOOL)
            executor.execute(SEQUENCE)
        }

        result.isSucceeded() shouldBe true
    }

    @Test
    fun `doAutotype should return error if sequence wasn't created`() {
        // arrange
        every {
            sequenceFactory.createAutotypeSequence(
                ENTRY1,
                DEFAULT_PATTERN,
                DELAY_BETWEEN_ACTIONS_IN_MILLIS
            )
        }.returns(null)

        // act
        val result = createUseCase()
            .doAutotype(
                executorType = AutotypeExecutorType.XDOTOOL,
                entry = ENTRY1,
                pattern = DEFAULT_PATTERN,
                delayBetweenActions = DELAY_BETWEEN_ACTIONS_IN_MILLIS,
                startDelayInSeconds = null
            )

        // assert
        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<AutokpassException>()
        result.getExceptionOrThrow().message shouldBe FAILED_TO_COMPILE_AUTOTYPE_SEQUENCE
    }

    @Test
    fun `doAutotype should make start delay if it was specified`() {
        // arrange
        val message = String.format(DELAY_MESSAGE, START_DELAY_IN_SECONDS)
        every {
            sequenceFactory.createAutotypeSequence(
                ENTRY1,
                DEFAULT_PATTERN,
                DELAY_BETWEEN_ACTIONS_IN_MILLIS
            )
        }.returns(SEQUENCE)
        every { executor.execute(SEQUENCE) }.returns(Unit)
        every { printer.println(message) }.returns(Unit)
        every { throttler.sleep(START_DELAY_IN_MILLIS) }.returns(Unit)
        every { autotypeExecutorFactory.getExecutor(AutotypeExecutorType.XDOTOOL) }.returns(executor)

        // act
        val result = createUseCase()
            .doAutotype(
                executorType = AutotypeExecutorType.XDOTOOL,
                entry = ENTRY1,
                pattern = DEFAULT_PATTERN,
                delayBetweenActions = DELAY_BETWEEN_ACTIONS_IN_MILLIS,
                startDelayInSeconds = START_DELAY_IN_SECONDS
            )

        // assert
        verifySequence {
            sequenceFactory.createAutotypeSequence(ENTRY1, DEFAULT_PATTERN, DELAY_BETWEEN_ACTIONS_IN_MILLIS)
            printer.println(message)
            throttler.sleep(START_DELAY_IN_MILLIS)
            autotypeExecutorFactory.getExecutor(AutotypeExecutorType.XDOTOOL)
            executor.execute(SEQUENCE)
        }

        result.isSucceeded() shouldBe true
    }

    private fun createUseCase(): AutotypeUseCase {
        return AutotypeUseCase(
            autotypeExecutorFactory = autotypeExecutorFactory,
            sequenceFactory = sequenceFactory,
            throttler = throttler,
            printer = printer
        )
    }

    companion object {
        private const val DELAY_BETWEEN_ACTIONS_IN_MILLIS = 200L
        private const val START_DELAY_IN_SECONDS = 3L
        private const val START_DELAY_IN_MILLIS = 3000L

        private val SEQUENCE = AutotypeSequenceFactory().createAutotypeSequence(
            ENTRY1,
            DEFAULT_PATTERN,
            DELAY_BETWEEN_ACTIONS_IN_MILLIS
        )!!
    }
}