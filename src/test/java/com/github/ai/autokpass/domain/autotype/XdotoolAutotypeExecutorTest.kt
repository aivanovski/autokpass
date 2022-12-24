package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.TestData.DEFAULT_AUTOTYPE_ITEMS
import com.github.ai.autokpass.TestData.DEFAULT_DELAY
import com.github.ai.autokpass.TestData.DEFAULT_INPUT_TEXT
import com.github.ai.autokpass.TestData.EXCEPTION
import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beTheSameInstanceAs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test

class XdotoolAutotypeExecutorTest {

    private val processExecutor = mockk<ProcessExecutor>()
    private val throttler = mockk<ThreadThrottler>()

    @Test
    fun `execute should run xdotool correctly`() {
        // arrange
        val sequence = AutotypeSequence(DEFAULT_AUTOTYPE_ITEMS)

        every { processExecutor.execute(any()) }.returns(Result.Success(EMPTY))
        every { throttler.sleep(any()) }.returns(Unit)

        // act
        val result = newExecutor().execute(sequence)

        // assert
        verifySequence {
            processExecutor.execute(TEXT_COMMAND)
            processExecutor.execute(TAB_COMMAND)
            processExecutor.execute(ENTER_COMMAND)
            throttler.sleep(DEFAULT_DELAY)
        }
        confirmVerified(processExecutor, throttler)

        result.isSucceeded() shouldBe true
    }

    @Test
    fun `execute should return error`() {
        // arrange
        val sequence = AutotypeSequence(DEFAULT_AUTOTYPE_ITEMS)

        every { processExecutor.execute(TEXT_COMMAND) }.returns(Result.Success(EMPTY))
        every { processExecutor.execute(TAB_COMMAND) }.returns(Result.Error(EXCEPTION))

        // act
        val result = newExecutor().execute(sequence)

        // assert
        verifySequence {
            processExecutor.execute(TEXT_COMMAND)
            processExecutor.execute(TAB_COMMAND)
        }
        with(result) {
            isFailed() shouldBe true
            getExceptionOrThrow() should beTheSameInstanceAs(EXCEPTION)
        }
    }

    private fun newExecutor(): XdotoolAutotypeExecutor {
        return XdotoolAutotypeExecutor(processExecutor, throttler)
    }

    companion object {
        private const val TEXT_COMMAND = "xdotool type $DEFAULT_INPUT_TEXT"
        private const val TAB_COMMAND = "xdotool key Tab"
        private const val ENTER_COMMAND = "xdotool key enter"
    }
}