package com.github.ai.autokpass.model

sealed class AutotypeState {
    data class CountDown(val secondsLeft: Int) : AutotypeState()
    object Autotyping : AutotypeState()
    object Finished : AutotypeState()
}