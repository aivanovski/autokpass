package com.github.ai.autokpass.presentation.ui.screens.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ai.autokpass.extensions.collectAsStateImmediately
import com.github.ai.autokpass.presentation.ui.core.CenteredBox
import com.github.ai.autokpass.presentation.ui.core.ErrorStateView
import com.github.ai.autokpass.presentation.ui.core.ProgressBar
import com.github.ai.autokpass.presentation.ui.core.TopBar
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import com.github.ai.autokpass.presentation.ui.core.theme.AppTextStyles
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockViewModel
import kotlinx.coroutines.Dispatchers

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val strings = StringResourcesImpl()
    val state by viewModel.state.collectAsStateImmediately()

    TopBar(
        title = "Settings",
        startContent = {
            Button(
                onClick = { viewModel.onCancelButtonClicked() },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "Cancel",
                    style = AppTextStyles.button
                )
            }
        },
        endContent = {
            Button(
                onClick = { viewModel.onSaveButtonClicked() },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "Save",
                    style = AppTextStyles.button
                )
            }
        }
    ) {
        with(state) {
            when (this) {
                is SettingsScreenState.Loading -> {
                    CenteredBox { ProgressBar() }
                }
                is SettingsScreenState.Error -> {
                    CenteredBox { ErrorStateView(message) }
                }
                is SettingsScreenState.Data -> {
                    ScreenContent(
                        state = this,
                        onFilePathChanged = { viewModel.onFilePathChanged(it) },
                        onKeyPathChanged = { viewModel.onKeyPathChanged(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    state: SettingsScreenState.Data,
    onFilePathChanged: (text: String) -> Unit,
    onKeyPathChanged: (text: String) -> Unit
) {
    OutlinedTextField(
        value = state.filePath,
        onValueChange = onFilePathChanged,
        textStyle = AppTextStyles.editor,
        label = { Text("Path to database") },
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp)
    )

    OutlinedTextField(
        value = state.keyPath,
        onValueChange = onKeyPathChanged,
        textStyle = AppTextStyles.editor,
        label = { Text("Key file") },
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp)
    )
}