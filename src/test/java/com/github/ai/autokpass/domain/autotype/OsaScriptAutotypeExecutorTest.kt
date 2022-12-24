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
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test

class OsaScriptAutotypeExecutorTest {

    private val processExecutor = mockk<ProcessExecutor>()
    private val throttler = mockk<ThreadThrottler>()

    @Test
    fun `execute should run osascript correctly`() {
        // arrange
        val sequence = AutotypeSequence(DEFAULT_AUTOTYPE_ITEMS)

        every { processExecutor.executeWithBash(any()) }.returns(Result.Success(EMPTY))
        every { throttler.sleep(any()) }.returns(Unit)

        // act
        val result = newExecutor().execute(sequence)

        // assert
        verifySequence {
            processExecutor.executeWithBash(TEXT_COMMAND)
            processExecutor.executeWithBash(TAB_COMMAND)
            processExecutor.executeWithBash(ENTER_COMMAND)
            throttler.sleep(DEFAULT_DELAY)
        }
        result.isSucceeded() shouldBe true
    }

    @Test
    fun `execute should return error`() {
        // arrange
        val sequence = AutotypeSequence(DEFAULT_AUTOTYPE_ITEMS)

        every { processExecutor.executeWithBash(TEXT_COMMAND) }.returns(Result.Success(EMPTY))
        every { processExecutor.executeWithBash(TAB_COMMAND) }.returns(Result.Error(EXCEPTION))

        // act
        val result = newExecutor().execute(sequence)

        // assert
        verifySequence {
            processExecutor.executeWithBash(TEXT_COMMAND)
            processExecutor.executeWithBash(TAB_COMMAND)
        }
        with(result) {
            isFailed() shouldBe true
            getExceptionOrThrow() should beTheSameInstanceAs(EXCEPTION)
        }
    }

    private fun newExecutor(): OsaScriptAutotypeExecutor {
        return OsaScriptAutotypeExecutor(processExecutor, throttler)
    }

    companion object {
        private const val ENTER_COMMAND =
            """echo "tell application \"System Events\" to key code 36" | osascript"""
        private const val TAB_COMMAND =
            """echo "tell application \"System Events\" to key code 48" | osascript"""
        private const val TEXT_COMMAND =
            """echo "tell application \"System Events\" to keystroke \"${DEFAULT_INPUT_TEXT}\"" | osascript"""
    }
}