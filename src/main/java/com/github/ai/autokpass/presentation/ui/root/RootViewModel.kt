package com.github.ai.autokpass.presentation.ui.root

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.flow.MutableStateFlow

class RootViewModel {

    val windowState = MutableStateFlow(
        WindowState(
            placement = WindowPlacement.Floating,
            isMinimized = false,
            position = WindowPosition(Alignment.BottomCenter),
            size = DpSize(1000.dp, 600.dp)
        )
    )
}