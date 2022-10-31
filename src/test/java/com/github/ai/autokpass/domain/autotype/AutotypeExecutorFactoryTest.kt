package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeExecutorType.CLICLICK
import com.github.ai.autokpass.model.AutotypeExecutorType.OSA_SCRIPT
import com.github.ai.autokpass.model.AutotypeExecutorType.XDOTOOL
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import io.mockk.mockk
import org.junit.jupiter.api.Test

class AutotypeExecutorFactoryTest {

    private val processExecutor = mockk<ProcessExecutor>()
    private val threadThrottler = mockk<ThreadThrottler>()

    @Test
    fun `getExecutor should return executor by type`() {
        val factory = AutotypeExecutorFactory(processExecutor, threadThrottler)

        factory.getExecutor(XDOTOOL) should beInstanceOf<XdotoolAutotypeExecutor>()
        factory.getExecutor(OSA_SCRIPT) should beInstanceOf<OsaScriptAutotypeExecutor>()
        factory.getExecutor(CLICLICK) should beInstanceOf<CliclickAutotypeExecutor>()
    }
}