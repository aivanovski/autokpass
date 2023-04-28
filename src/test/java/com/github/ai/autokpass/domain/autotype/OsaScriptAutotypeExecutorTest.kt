package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.TestData.DEFAULT_AUTOTYPE_ITEMS
import com.github.ai.autokpass.TestData.DEFAULT_DELAY
import com.github.ai.autokpass.TestData.DEFAULT_INPUT_TEXT
import com.github.ai.autokpass.TestData.EXCEPTION
import com.github.ai.autokpass.domain.autotype.OsaScriptAutotypeExecutor.Companion.ENTER_COMMAND
import com.github.ai.autokpass.domain.autotype.OsaScriptAutotypeExecutor.Companion.TAB_COMMAND
import com.github.ai.autokpass.domain.autotype.OsaScriptAutotypeExecutor.Companion.TEXT_COMMAND
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
        val textCommand = String.format(TEXT_COMMAND, DEFAULT_INPUT_TEXT)

        every { processExecutor.executeWithBash(any()) }.returns(Result.Success(EMPTY))
        every { throttler.sleep(any()) }.returns(Unit)

        // act
        val result = newExecutor().execute(sequence)

        // assert
        verifySequence {
            processExecutor.executeWithBash(textCommand)
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
        val textCommand = String.format(TEXT_COMMAND, DEFAULT_INPUT_TEXT)

        every { processExecutor.executeWithBash(textCommand) }.returns(Result.Success(EMPTY))
        every { processExecutor.executeWithBash(TAB_COMMAND) }.returns(Result.Error(EXCEPTION))

        // act
        val result = newExecutor().execute(sequence)

        // assert
        verifySequence {
            processExecutor.executeWithBash(textCommand)
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
}