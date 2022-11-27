package com.github.ai.autokpass.domain.coroutine

import kotlinx.coroutines.CoroutineDispatcher

interface Dispatchers {
    val Main: CoroutineDispatcher
    val IO: CoroutineDispatcher
}