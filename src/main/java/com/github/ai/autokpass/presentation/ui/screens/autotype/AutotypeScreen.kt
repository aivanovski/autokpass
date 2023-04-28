package com.github.ai.autokpass.presentation.ui.screens.autotype

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.presentation.ui.core.ErrorStateView
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.core.theme.AppTextStyles
import com.github.ai.autokpass.presentation.ui.screens.autotype.AutotypeViewModel.ScreenState

@Composable
fun AutotypeScreen(viewModel: AutotypeViewModel) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        with(state) {
            when (this) {
                is ScreenState.Data -> {
                    ScreenContent(
                        message = message,
                        isCancelButtonVisible = isCancelButtonVisible,
                        onCancelClicked = { viewModel.onCancelClicked() }
                    )
                }

                is ScreenState.Error -> {
                    ErrorStateView(message = message)
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    message: String,
    isCancelButtonVisible: Boolean,
    onCancelClicked: () -> Unit
) {
    val strings: StringResources = get()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    style = AppTextStyles.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (isCancelButtonVisible) {
                Button(
                    onClick = onCancelClicked,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = strings.cancel,
                        style = AppTextStyles.button
                    )
                }
            }
        }
    }
}