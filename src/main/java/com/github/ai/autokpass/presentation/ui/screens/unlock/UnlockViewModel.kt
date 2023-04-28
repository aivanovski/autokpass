package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.model.ParsedArgs
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
    private val args: ParsedArgs
) : CoroutineViewModel(dispatchers) {

    private var password = EMPTY
    private var error: String? = null
    private var isPasswordVisible = false

    private val _state = MutableStateFlow(createScreenState())
    val state: StateFlow<ScreenState> = _state

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

    private fun navigateToMain(key: PasswordKey) {
        router.navigateTo(
            Screen.SelectEntry(
                args = SelectEntryArgs(key)
            )
        )
    }

    private fun updateScreenState() {
        _state.value = createScreenState()
    }

    private fun createScreenState(): ScreenState =
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
    }
}