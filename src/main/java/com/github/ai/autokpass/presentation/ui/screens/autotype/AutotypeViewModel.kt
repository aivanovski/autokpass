package com.github.ai.autokpass.presentation.ui.screens.autotype

import androidx.compose.ui.unit.dp
import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.model.AutotypeState
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.root.RootViewModel
import com.github.ai.autokpass.util.StringUtils.EMPTY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AutotypeViewModel(
    private val interactor: AutotypeInteractor,
    private val errorInteractor: ErrorInteractor,
    dispatchers: Dispatchers,
    private val strings: StringResources,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: AutotypeArgs,
    private val appArgs: ParsedArgs
) : CoroutineViewModel(dispatchers) {

    private val _state = MutableStateFlow<ScreenState>(initState())
    val state: StateFlow<ScreenState> = _state

    override fun start() {
        super.start()

        rootViewModel.updateWindowSize(height = 140.dp)

        viewModelScope.launch {
            val isAbleToAwaitResult = interactor.isAbleToAwaitWindowChanged(appArgs.autotypeType)
            if (isAbleToAwaitResult.isFailed()) {
                _state.value = ScreenState.Error(
                    message = errorInteractor.processAndGetMessage(isAbleToAwaitResult.asErrorOrThrow())
                )
                return@launch
            }

            val isAbleToAwait = isAbleToAwaitResult.getDataOrThrow()
            if (isAbleToAwait) {
                _state.value = ScreenState.Data(
                    message = strings.autotypeSelectWindowMessage,
                    isCancelButtonVisible = true
                )

                val awaitResult = interactor.awaitWindowFocusChanged()
                if (awaitResult.isFailed()) {
                    _state.value = ScreenState.Error(
                        message = errorInteractor.processAndGetMessage(awaitResult.asErrorOrThrow())
                    )
                    return@launch
                }
            }

            interactor.buildAutotypeFlow(
                appArgs = appArgs,
                entry = args.entry,
                pattern = args.pattern,
                startDelayInMillis = appArgs.startDelayInMillis,
                delayBetweenActionsInMillis = appArgs.delayBetweenActionsInMillis
            )
                .collect { autotypeStateResult ->
                    if (autotypeStateResult.isFailed()) {
                        _state.value = ScreenState.Error(
                            message = errorInteractor.processAndGetMessage(autotypeStateResult.asErrorOrThrow())
                        )
                    } else {
                        when (val autotypeState = autotypeStateResult.getDataOrThrow()) {
                            is AutotypeState.CountDown -> {
                                _state.value = ScreenState.Data(
                                    message = String.format(
                                        strings.autotypeCountDownMessage,
                                        autotypeState.secondsLeft
                                    ),
                                    isCancelButtonVisible = false
                                )
                            }

                            is AutotypeState.Autotyping -> {
                                _state.value = ScreenState.Data(
                                    message = strings.autotyping,
                                    isCancelButtonVisible = false
                                )
                            }

                            is AutotypeState.Finished -> {
                                router.exit()
                            }
                        }
                    }
                }
        }
    }

    fun onCancelClicked() {
        router.exit()
    }

    private fun initState(): ScreenState.Data =
        ScreenState.Data(
            message = EMPTY,
            isCancelButtonVisible = false
        )

    sealed class ScreenState {
        data class Error(val message: String) : ScreenState()
        data class Data(
            val message: String,
            val isCancelButtonVisible: Boolean
        ) : ScreenState()
    }
}