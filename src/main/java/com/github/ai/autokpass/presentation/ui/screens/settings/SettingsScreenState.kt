package com.github.ai.autokpass.presentation.ui.screens.settings

import com.github.ai.autokpass.model.AutotypeExecutorType

sealed class SettingsScreenState {

    object Loading : SettingsScreenState()

    data class Data(
        val filePath: String,
        val keyPath: String,
        val delay: String,
        val delayBetweenActions: String,
        val command: String,
        val selectedAutotypeType: AutotypeExecutorType,
        val availableAutotypeTypes: List<AutotypeExecutorType>
    ) : SettingsScreenState()

    data class Error(
        val message: String
    ) : SettingsScreenState()
}