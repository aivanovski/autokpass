package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryArgs
import com.github.ai.autokpass.util.StringUtils.EMPTY
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class UnlockViewModel(
    private val interactor: UnlockInteractor,
    private val errorInteractor: ErrorInteractor,
    dispatchers: Dispatchers,
    private val router: Router
) : CoroutineViewModel(dispatchers) {

    private var config: ParsedConfig? = null
    private val intents = Channel<UnlockIntent>()

    private val _state = MutableStateFlow<UnlockState>(UnlockState.Loading)
    val state: StateFlow<UnlockState> = _state

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun start() {
        viewModelScope.launch {
            intents.receiveAsFlow()
                .onStart { emit(UnlockIntent.Init) }
                .flatMapLatest { intent -> handleIntent(intent, _state.value) }
                .collect { state ->
                    _state.value = state
                }
        }
    }

    fun sendIntent(intent: UnlockIntent) {
        if (intent.isImmediate) {
            handleIntent(intent, _state.value)
            return
        }

        viewModelScope.launch {
            intents.send(intent)
        }
    }

    private fun handleIntent(
        intent: UnlockIntent,
        currentState: UnlockState
    ): Flow<UnlockState> {
        return when (intent) {
            is UnlockIntent.Init -> loadData()

            is UnlockIntent.OnUnlockButtonClicked -> unlockDatabase(currentState)

            is UnlockIntent.OnPasswordInputChanged -> {
                _state.value = (currentState as UnlockState.Data)
                    .copy(
                        password = intent.text,
                        error = null
                    )

                flowOf()
            }

            is UnlockIntent.OnPasswordVisibilityChanged -> {
                val newState = (currentState as UnlockState.Data)
                    .copy(
                        isPasswordVisible = !currentState.isPasswordVisible
                    )

                flowOf(newState)
            }

            is UnlockIntent.OnErrorIconClicked -> {
                val newState = (currentState as UnlockState.Data)
                    .copy(
                        error = null
                    )

                flowOf(newState)
            }
        }
    }

    private fun loadData(): Flow<UnlockState> {
        return flow {
            emit(UnlockState.Loading)

            val loadConfigResult = interactor.loadConfig()
            if (loadConfigResult.isFailed()) {
                emit(newErrorState(loadConfigResult.asErrorOrThrow()))
                return@flow
            }

            config = loadConfigResult.getDataOrThrow()
            val config = loadConfigResult.getDataOrThrow()
            val fileKey = config.getFileKeyOrNull()

            if (fileKey != null) {
                val unlockResult = interactor.unlockDatabase(fileKey, config.filePath)
                if (unlockResult.isFailed()) {
                    emit(newErrorState(unlockResult.asErrorOrThrow()))
                    return@flow
                }

                navigateToMain(fileKey)
                return@flow
            }

            emit(
                UnlockState.Data(
                    password = EMPTY,
                    error = null,
                    isPasswordVisible = false
                )
            )
        }
    }

    private fun ParsedConfig.getFileKeyOrNull(): KeepassKey.FileKey? {
        return if (this.keyPath != null) {
            KeepassKey.FileKey(
                file = File(keyPath),
                processingCommand = keyProcessingCommand
            )
        } else {
            null
        }
    }

    private fun unlockDatabase(state: UnlockState): Flow<UnlockState> {
        return flow {
            emit(UnlockState.Loading)

            val filePath = config?.filePath ?: EMPTY

            val dataState = (state as UnlockState.Data)
            val password = dataState.password
            val isPasswordVisible = dataState.isPasswordVisible

            val key = PasswordKey(password)

            val unlockResult = interactor.unlockDatabase(key, filePath)
            if (unlockResult.isSucceeded()) {
                navigateToMain(PasswordKey(password))
            } else {
                emit(
                    UnlockState.Data(
                        password = password,
                        error = errorInteractor.processAndGetMessage(unlockResult.asErrorOrThrow()),
                        isPasswordVisible = isPasswordVisible
                    )
                )
            }
        }
    }

    private fun navigateToMain(key: KeepassKey) {
        router.navigateTo(
            Screen.SelectEntry(
                args = SelectEntryArgs(key)
            )
        )
    }

    private fun newErrorState(error: Result.Error): UnlockState =
        UnlockState.Error(
            message = errorInteractor.processAndGetMessage(error)
        )

    sealed class UnlockState {

        object Loading : UnlockState()

        data class Data(
            val password: String,
            val error: String?,
            val isPasswordVisible: Boolean
        ) : UnlockState()

        data class Error(
            val message: String
        ) : UnlockState()
    }

    sealed class UnlockIntent(
        val isImmediate: Boolean = false
    ) {
        object Init : UnlockIntent()

        data class OnPasswordInputChanged(
            val text: String
        ) : UnlockIntent(isImmediate = true)

        object OnPasswordVisibilityChanged : UnlockIntent()

        object OnUnlockButtonClicked : UnlockIntent()

        object OnErrorIconClicked : UnlockIntent()
    }
}