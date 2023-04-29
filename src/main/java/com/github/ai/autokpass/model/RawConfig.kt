package com.github.ai.autokpass.model

data class RawConfig(
    val filePath: String?,
    val keyPath: String?,
    val startDelay: String?,
    val delayBetweenActions: String?,
    val autotypeType: String?,
    val keyProcessingCommand: String?
) {

    fun isEmpty(): Boolean {
        return filePath.isNullOrEmpty() &&
            keyPath.isNullOrEmpty() &&
            startDelay.isNullOrEmpty() &&
            delayBetweenActions.isNullOrEmpty() &&
            autotypeType.isNullOrEmpty() &&
            keyProcessingCommand.isNullOrEmpty()
    }

    companion object {
        val EMPTY = RawConfig(
            filePath = null,
            keyPath = null,
            startDelay = null,
            delayBetweenActions = null,
            autotypeType = null,
            keyProcessingCommand = null
        )
    }
}