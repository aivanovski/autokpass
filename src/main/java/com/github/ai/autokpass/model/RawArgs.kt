package com.github.ai.autokpass.model

data class RawArgs(
    val filePath: String?,
    val keyPath: String?,
    val startDelay: String?,
    val delayBetweenActions: String?,
    val inputType: String?,
    val autotypeType: String?,
    val keyProcessingCommand: String?
) {

    fun isEmpty(): Boolean {
        return filePath.isNullOrEmpty() &&
            keyPath.isNullOrEmpty() &&
            startDelay.isNullOrEmpty() &&
            delayBetweenActions.isNullOrEmpty() &&
            inputType.isNullOrEmpty() &&
            autotypeType.isNullOrEmpty() &&
            keyProcessingCommand.isNullOrEmpty()
    }
}