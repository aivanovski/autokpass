package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.AutotypeSequenceItem
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.Test

class XdotoolAutotypeExecutorTest {

    @Test
    fun `execute should run xdotool correctly`() {
        // arrange
        val processExecutor = mockk<ProcessExecutor>()
        val throttler = mockk<ThreadThrottler>()
        val sequence = AutotypeSequence(
            items = listOf(
                AutotypeSequenceItem.Text(INPUT_TEXT),
                AutotypeSequenceItem.Tab,
                AutotypeSequenceItem.Enter,
                AutotypeSequenceItem.Delay(DELAY)
            )
        )

        every { processExecutor.execute(any()) }.returns(EMPTY)
        every { throttler.sleep(any()) }.returns(Unit)

        // act
        XdotoolAutotypeExecutor(processExecutor, throttler)
            .execute(sequence)

        // assert
        verifySequence {
            processExecutor.execute("xdotool type $INPUT_TEXT")
            processExecutor.execute("xdotool key Tab")
            processExecutor.execute("xdotool key enter")
        }
        verify {
            throttler.sleep(DELAY)
        }
        confirmVerified(processExecutor, throttler)
    }

    companion object {
        private const val INPUT_TEXT = "abc123"
        private const val DELAY = 100L
    }
}