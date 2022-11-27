package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _password = MutableStateFlow(EMPTY)
    val password: StateFlow<String> = _password

    val isLoading = MutableStateFlow(false)
    val isPasswordVisible = MutableStateFlow(false)

    fun onPasswordInputChanged(text: String) {
        _password.value = text
        if (error.value != null) {
            _error.value = null
        }
    }

    fun togglePasswordVisibility() {
        isPasswordVisible.value = !isPasswordVisible.value
    }

    fun removeError() {
        _error.value = null
    }

    fun unlockDatabase() {
        isLoading.value = true

        val password = this._password.value

        viewModelScope.launch {
            val unlockResult = interactor.unlockDatabase(password, args.filePath)
            if (unlockResult.isSucceeded()) {
                navigateToMain(PasswordKey(password))
            } else {
                _error.value = errorInteractor.processAndGetMessage(unlockResult.asErrorOrThrow())
                isLoading.value = false
            }
        }
    }

    private fun navigateToMain(key: PasswordKey) {
        router.navigateTo(Screen.SelectEntry(key))
    }
}