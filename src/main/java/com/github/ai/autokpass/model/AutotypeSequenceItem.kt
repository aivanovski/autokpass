package com.github.ai.autokpass.model

sealed class AutotypeSequenceItem {
    object Tab : AutotypeSequenceItem()
    object Enter : AutotypeSequenceItem()
    data class Text(val text: String) : AutotypeSequenceItem()
    data class Delay(val millis: Long) : AutotypeSequenceItem()
}