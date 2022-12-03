package com.github.ai.autokpass.presentation.ui.screens.termination

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ai.autokpass.presentation.ui.core.CenteredColumn
import com.github.ai.autokpass.presentation.ui.core.ErrorStateView
import com.github.ai.autokpass.presentation.ui.core.theme.AppTextStyles
import com.github.ai.autokpass.presentation.ui.screens.termination.TerminationViewModel.ScreenState

@Composable
fun TerminationScreen(viewModel: TerminationViewModel) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        with(state) {
            when (this) {
                is ScreenState.Error -> {
                    CenteredColumn {
                        ErrorStateView(
                            message = message
                        )

                        Button(
                            modifier = Modifier.padding(top = 8.dp),
                            onClick = { viewModel.onExitButtonClicked() }
                        ) {
                            Text(
                                text = "Exit",
                                style = AppTextStyles.button
                            )
                        }
                    }
                }
            }
        }
    }
}