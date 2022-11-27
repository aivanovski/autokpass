package com.github.ai.autokpass.presentation.ui.core

import com.github.ai.autokpass.domain.coroutine.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class CoroutineViewModel(dispatchers: Dispatchers) {

    protected val viewModelScope = CoroutineScope(dispatchers.Main + SupervisorJob())

    open fun start() {
    }

    open fun stop() {
        viewModelScope.cancel()
    }
}