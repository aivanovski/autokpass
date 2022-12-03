package com.github.ai.autokpass.presentation.ui.screens.termination

import androidx.compose.ui.unit.dp
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.root.RootViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TerminationViewModel(
    dispatchers: Dispatchers,
    private val rootViewModel: RootViewModel,
    private val router: Router,
    private val args: TerminationArgs
) : CoroutineViewModel(dispatchers) {

    private val _state = MutableStateFlow(packState())
    val state: StateFlow<ScreenState> = _state

    override fun start() {
        super.start()
        rootViewModel.updateWindowSize(height = 600.dp)
    }

    fun onExitButtonClicked() {
        router.exit()
    }

    private fun packState(): ScreenState {
        return ScreenState.Error(
            message = args.errorMessage
        )
    }

    sealed class ScreenState {
        data class Error(val message: String) : ScreenState()
    }
}