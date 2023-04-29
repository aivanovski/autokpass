package com.github.ai.autokpass.extensions

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel

fun Lifecycle.registerViewModel(viewModel: CoroutineViewModel) {
    val observer = object : Lifecycle.Callbacks {
        override fun onCreate() {
            viewModel.start()
        }

        override fun onDestroy() {
            unsubscribe(this)
            viewModel.stop()
        }
    }

    subscribe(observer)
}