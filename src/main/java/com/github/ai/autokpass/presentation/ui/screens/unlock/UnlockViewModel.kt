package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryArgs
import com.github.ai.autokpass.util.StringUtils.EMPTY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UnlockViewModel(
    private val interactor: UnlockInteractor,
    private val errorInteractor: ErrorInteractor,
    dispatchers: Dispatchers,
    private val router: Router,
    private val args: ParsedConfig
) : CoroutineViewModel(dispatchers) {

    private var password = EMPTY
    private var error: String? = null
    private var isPasswordVisible = false

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val state: StateFlow<ScreenState> = _state

    override fun start() {
        super.start()
        viewModelScope.launch {
            interactor.loadConfig()
                .collect(::onConfigUpdated)
        }
    }

    private fun onConfigUpdated(result: Result<ParsedConfig>) {
        if (result.isFailed()) {
            val message = errorInteractor.processAndGetMessage(result.asErrorOrThrow())
            _state.value = ScreenState.Error(message)
        } else {
            _state.value = createDataState()
        }
    }

    fun onPasswordInputChanged(text: String) {
        password = text
        if (error != null) {
            error = null
        }

        updateScreenState()
    }

    fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        updateScreenState()
    }

    fun clearError() {
        error = null
        updateScreenState()
    }

    fun unlockDatabase() {
        _state.value = ScreenState.Loading

        viewModelScope.launch {
            val unlockResult = interactor.unlockDatabase(password, args.filePath)
            if (unlockResult.isSucceeded()) {
                navigateToMain(PasswordKey(password))
            } else {
                error = errorInteractor.processAndGetMessage(unlockResult.asErrorOrThrow())
            }

            updateScreenState()
        }
    }

    fun onSettingsButtonClicked() {
        router.navigateTo(Screen.Settings)
    }

    private fun navigateToMain(key: PasswordKey) {
        router.navigateTo(
            Screen.SelectEntry(
                args = SelectEntryArgs(key)
            )
        )
    }

    private fun updateScreenState() {
        _state.value = createDataState()
    }

    private fun createDataState(): ScreenState =
        ScreenState.Data(
            password = password,
            error = error,
            isPasswordVisible = isPasswordVisible
        )

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Data(
            val password: String,
            val error: String?,
            val isPasswordVisible: Boolean
        ) : ScreenState()

        data class Error(
            val message: String
        ) : ScreenState()
    }
}