package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.jupiter.api.Test

class AutotypeExecutorFactoryTest {

    private val processExecutor = mockk<ProcessExecutor>()
    private val threadThrottler = mockk<ThreadThrottler>()

    @Test
    fun `getExecutor should return executor by type`() {
        val factory = AutotypeExecutorFactory(processExecutor, threadThrottler)

        assertThat(factory.getExecutor(AutotypeExecutorType.XDOTOOL))
            .isInstanceOf(XdotoolAutotypeExecutor::class.java)

        assertThat(factory.getExecutor(AutotypeExecutorType.OSA_SCRIPT))
            .isInstanceOf(OsaScriptAutotypeExecutor::class.java)

        assertThat(factory.getExecutor(AutotypeExecutorType.CLICLICK))
            .isInstanceOf(CliclickAutotypeExecutor::class.java)
    }
}