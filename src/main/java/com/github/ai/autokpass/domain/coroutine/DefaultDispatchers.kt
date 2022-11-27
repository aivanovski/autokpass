package com.github.ai.autokpass.domain.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers as CoroutineDispatchers

class DefaultDispatchers : Dispatchers {
    override val Main: CoroutineDispatcher = CoroutineDispatchers.Main
    override val IO: CoroutineDispatcher = CoroutineDispatchers.IO
}