package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.TestData.DEFAULT_AUTOTYPE_ITEMS
import com.github.ai.autokpass.TestData.DEFAULT_DELAY
import com.github.ai.autokpass.TestData.DEFAULT_INPUT_TEXT
import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test

class XdotoolAutotypeExecutorTest {

    @Test
    fun `execute should run xdotool correctly`() {
        // arrange
        val processExecutor = mockk<ProcessExecutor>()
        val throttler = mockk<ThreadThrottler>()
        val sequence = AutotypeSequence(DEFAULT_AUTOTYPE_ITEMS)

        every { processExecutor.execute(any()) }.returns(Result.Success(EMPTY))
        every { throttler.sleep(any()) }.returns(Unit)

        // act
        XdotoolAutotypeExecutor(processExecutor, throttler)
            .execute(sequence)

        // assert
        verifySequence {
            processExecutor.execute("xdotool type $DEFAULT_INPUT_TEXT")
            processExecutor.execute("xdotool key Tab")
            processExecutor.execute("xdotool key enter")
            throttler.sleep(DEFAULT_DELAY)
        }
        confirmVerified(processExecutor, throttler)
    }
}