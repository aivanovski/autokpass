package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypeExecutorType
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Test

class AutotypeExecutorProviderTest {

    @Test
    fun `getExecutor should return executor by type`() {
        // arrange
        val executor = mockk<AutotypeExecutor>()
        val executors = mapOf(
            AutotypeExecutorType.XDOTOOL to executor
        )

        // act
        val result = AutotypeExecutorProvider(executors)
            .getExecutor(AutotypeExecutorType.XDOTOOL)

        // assert
        assertThat(result).isSameInstanceAs(executor)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getExecutor should throw exception if executor not found`() {
        // arrange
        val executor = mockk<AutotypeExecutor>()
        val executors = emptyMap<AutotypeExecutorType, AutotypeExecutor>()

        // act
        val result = AutotypeExecutorProvider(executors)
            .getExecutor(AutotypeExecutorType.XDOTOOL)
    }
}