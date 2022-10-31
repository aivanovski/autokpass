package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.TestData.DEFAULT_AUTOTYPE_ITEMS
import com.github.ai.autokpass.TestData.DEFAULT_DELAY
import com.github.ai.autokpass.TestData.DEFAULT_INPUT_TEXT
import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import com.github.ai.autokpass.util.StringUtils
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test

class OsaScriptAutotypeExecutorTest {

    @Test
    fun `execute should run osascript correctly`() {
        // arrange
        val processExecutor = mockk<ProcessExecutor>()
        val throttler = mockk<ThreadThrottler>()
        val sequence = AutotypeSequence(DEFAULT_AUTOTYPE_ITEMS)

        every { processExecutor.executeWithBash(any()) }.returns(StringUtils.EMPTY)
        every { throttler.sleep(any()) }.returns(Unit)

        // act
        OsaScriptAutotypeExecutor(processExecutor, throttler)
            .execute(sequence)

        // assert
        verifySequence {
            processExecutor.executeWithBash(TEXT_KEY_COMMAND)
            processExecutor.executeWithBash(TAB_KEY_COMMAND)
            processExecutor.executeWithBash(ENTER_KEY_COMMAND)
            throttler.sleep(DEFAULT_DELAY)
        }
        confirmVerified(processExecutor, throttler)
    }

    companion object {
        private const val ENTER_KEY_COMMAND =
            """echo "tell application \"System Events\" to key code 36" | osascript"""
        private const val TAB_KEY_COMMAND =
            """echo "tell application \"System Events\" to key code 48" | osascript"""
        private const val TEXT_KEY_COMMAND =
            """echo "tell application \"System Events\" to keystroke \"${DEFAULT_INPUT_TEXT}\"" | osascript"""
    }
}