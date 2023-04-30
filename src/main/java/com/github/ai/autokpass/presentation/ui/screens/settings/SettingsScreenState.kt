package com.github.ai.autokpass.presentation.ui.screens.settings

sealed class SettingsScreenState {

    object Loading : SettingsScreenState()

    data class Data(
        val filePath: String,
        val keyPath: String,
        val delay: String,
        val delayBetweenActions: String,
        val autotype: String,
        val command: String
    ) : SettingsScreenState()

    data class Error(
        val message: String
    ) : SettingsScreenState()
}