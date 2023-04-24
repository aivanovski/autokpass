package com.github.ai.autokpass.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> StateFlow<T>.collectAsStateImmediately(): State<T> {
    return this.collectAsState(
        context = Dispatchers.Main.immediate
    )
}