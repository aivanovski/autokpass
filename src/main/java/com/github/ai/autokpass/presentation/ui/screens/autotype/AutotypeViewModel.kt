package com.github.ai.autokpass.presentation.ui.screens.autotype

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory.Companion.DEFAULT_DELAY_BETWEEN_ACTIONS
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.model.AutotypeState
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.root.RootViewModel
import com.github.ai.autokpass.util.StringUtils.EMPTY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AutotypeViewModel(
    private val interactor: AutotypeInteractor,
    private val errorInteractor: ErrorInteractor,
    dispatchers: Dispatchers,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: AutotypeArgs,
    private val appArgs: ParsedArgs
) : CoroutineViewModel(dispatchers) {

    private val _state = MutableStateFlow<ScreenState>(initState())
    val state: StateFlow<ScreenState> = _state

    override fun start() {
        super.start()

        rootViewModel.windowState.value = WindowState(
            placement = WindowPlacement.Floating,
            isMinimized = false,
            position = WindowPosition(Alignment.BottomCenter),
            size = DpSize(1000.dp, 140.dp)
        )

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
                    message = "Please select window to start autotype",
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
                delayBetweenActionsInMillis = appArgs.autotypeDelayInMillis ?: DEFAULT_DELAY_BETWEEN_ACTIONS,
                startDelayInSeconds = appArgs.delayInSeconds
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
                                    message = "Autotype will start in ${autotypeState.secondsLeft} second(s)",
                                    isCancelButtonVisible = false
                                )
                            }

                            is AutotypeState.Autotyping -> {
                                _state.value = ScreenState.Data(
                                    message = "Autotyping",
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