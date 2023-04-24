package com.github.ai.autokpass.utils

import com.github.ai.autokpass.domain.coroutine.Dispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class TestDispatchers : Dispatchers {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val Main: CoroutineDispatcher = UnconfinedTestDispatcher()
    @OptIn(ExperimentalCoroutinesApi::class)
    override val IO: CoroutineDispatcher = UnconfinedTestDispatcher()
}