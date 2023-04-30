package com.github.ai.autokpass.presentation.ui.screens.settings

import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.util.StringUtils.EMPTY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val interactor: SettingsInteractor,
    private val errorInteractor: ErrorInteractor,
    private val dispatchers: Dispatchers,
    private val router: Router
) : CoroutineViewModel(dispatchers) {

    val state = MutableStateFlow<SettingsScreenState>(SettingsScreenState.Loading)

    override fun start() {
        super.start()
        viewModelScope.launch {
            val loadConfigResult = interactor.loadConfig()
            if (loadConfigResult.isSucceeded()) {
                val config = loadConfigResult.getDataOrThrow()

                state.value = SettingsScreenState.Data(
                    filePath = config.filePath,
                    keyPath = config.keyPath ?: EMPTY,
                    delay = config.startDelayInMillis.toString(),
                    delayBetweenActions = config.delayBetweenActionsInMillis.toString(),
                    autotype = config.autotypeType?.cliName ?: EMPTY,
                    command = config.keyProcessingCommand ?: EMPTY
                )
            } else {
                state.value = SettingsScreenState.Error(
                    message = errorInteractor.processAndGetMessage(loadConfigResult.asErrorOrThrow())
                )
            }
        }
    }

    fun onCancelButtonClicked() {
        router.back()
    }

    fun onSaveButtonClicked() {
        val currentState = (state.value as? SettingsScreenState.Data) ?: return

        val config = ParsedConfig(
            filePath = currentState.filePath,
            keyPath = currentState.keyPath,
            startDelayInMillis = 3000L,
            delayBetweenActionsInMillis = 200L,
            autotypeType = null,
            keyProcessingCommand = null
        )


        viewModelScope.launch {
            val saveResult = interactor.saveConfig(config)
            if (saveResult.isSucceeded()) {
                router.back()
            } else {
                state.value = SettingsScreenState.Error(
                    message = errorInteractor.processAndGetMessage(saveResult.asErrorOrThrow())
                )
            }
        }
    }

    fun onFilePathChanged(filePath: String) {
        val currentState = (state.value as? SettingsScreenState.Data) ?: return
        state.value = currentState.copy(filePath = filePath)
    }

    fun onKeyPathChanged(keyPath: String) {
        val currentState = (state.value as? SettingsScreenState.Data) ?: return
        state.value = currentState.copy(keyPath = keyPath)
    }
}