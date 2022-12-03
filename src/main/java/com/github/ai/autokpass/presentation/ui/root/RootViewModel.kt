package com.github.ai.autokpass.presentation.ui.root

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RootViewModel {

    private val _windowState = MutableStateFlow(newWindowState(width = 1000.dp, height = 600.dp))
    val windowState: StateFlow<WindowState> = _windowState

    fun updateWindowSize(width: Dp? = null, height: Dp? = null) {
        val currentWidth = _windowState.value.size.width
        val currentHeight = _windowState.value.size.height
        if (currentWidth == width && currentHeight == height) {
            return
        }

        val w = width ?: currentWidth
        val h = height ?: currentHeight

        _windowState.value = newWindowState(width = w, height = h)
    }

    private fun newWindowState(width: Dp, height: Dp): WindowState {
        return WindowState(
            placement = WindowPlacement.Floating,
            isMinimized = false,
            position = WindowPosition(Alignment.BottomCenter),
            size = DpSize(width, height)
        )
    }
}