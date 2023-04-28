package com.github.ai.autokpass.model

sealed class AutotypeState {
    data class CountDown(val secondsLeft: String) : AutotypeState()
    object Autotyping : AutotypeState()
    object Finished : AutotypeState()
}