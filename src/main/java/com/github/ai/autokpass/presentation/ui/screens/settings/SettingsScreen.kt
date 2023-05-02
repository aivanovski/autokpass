package com.github.ai.autokpass.presentation.ui.screens.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.ai.autokpass.extensions.collectAsStateImmediately
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.presentation.ui.core.CenteredBox
import com.github.ai.autokpass.presentation.ui.core.ErrorStateView
import com.github.ai.autokpass.presentation.ui.core.PreviewWithBackground
import com.github.ai.autokpass.presentation.ui.core.ProgressBar
import com.github.ai.autokpass.presentation.ui.core.TopBar
import com.github.ai.autokpass.presentation.ui.core.theme.AppColors
import com.github.ai.autokpass.presentation.ui.core.theme.AppTextStyles
import com.github.ai.autokpass.presentation.ui.screens.settings.views.AutotypeSelectorView

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsStateImmediately()

    ScreenContent(
        state = state,
        onCancelButtonClicked = viewModel::onCancelButtonClicked,
        onSaveButtonClicked = viewModel::onSaveButtonClicked,
        onFilePathChanged = viewModel::onFilePathChanged,
        onKeyPathChanged = viewModel::onKeyPathChanged,
        onDelayChanged = viewModel::onDelayChanged,
        onDelayBetweenActionsChanged = viewModel::onDelayBetweenActionsChanged,
        onCommandChanged = viewModel::onCommandChanged,
        onAutotypeChanged = viewModel::onAutotypeChanged
    )
}

@Composable
private fun ScreenContent(
    state: SettingsScreenState,
    onCancelButtonClicked: () -> Unit,
    onSaveButtonClicked: () -> Unit,
    onFilePathChanged: (text: String) -> Unit,
    onKeyPathChanged: (text: String) -> Unit,
    onDelayChanged: (text: String) -> Unit,
    onDelayBetweenActionsChanged: (text: String) -> Unit,
    onCommandChanged: (text: String) -> Unit,
    onAutotypeChanged: (type: AutotypeExecutorType) -> Unit
) {
    TopBar(
        title = "Settings",
        startContent = {
            Button(
                onClick = onCancelButtonClicked,
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
                onClick = onSaveButtonClicked,
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
                    DataContent(
                        state = this,
                        onFilePathChanged = onFilePathChanged,
                        onKeyPathChanged = onKeyPathChanged,
                        onDelayChanged = onDelayChanged,
                        onDelayBetweenActionsChanged = onDelayBetweenActionsChanged,
                        onCommandChanged = onCommandChanged,
                        onAutotypeChanged = onAutotypeChanged,
                        onCommandInfoIconClicked = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun DataContent(
    state: SettingsScreenState.Data,
    onFilePathChanged: (text: String) -> Unit,
    onKeyPathChanged: (text: String) -> Unit,
    onDelayChanged: (text: String) -> Unit,
    onDelayBetweenActionsChanged: (text: String) -> Unit,
    onCommandChanged: (text: String) -> Unit,
    onAutotypeChanged: (type: AutotypeExecutorType) -> Unit,
    onCommandInfoIconClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        OutlinedTextField(
            value = state.filePath,
            onValueChange = onFilePathChanged,
            textStyle = AppTextStyles.editor,
            label = { Text("Path to database") },
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .defaultMinSize(minWidth = 400.dp)
        )

        OutlinedTextField(
            value = state.keyPath,
            onValueChange = onKeyPathChanged,
            textStyle = AppTextStyles.editor,
            label = { Text("Key file") },
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                .defaultMinSize(minWidth = 400.dp)
        )

        OutlinedTextField(
            value = state.delay,
            onValueChange = onDelayChanged,
            textStyle = AppTextStyles.editor,
            label = { Text("Autotype start delay") },
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                .defaultMinSize(minWidth = 400.dp)
        )

        Text(
            text = "Delay in milliseconds to wait before starting autotype.",
            style = AppTextStyles.hint,
            modifier = Modifier
                .padding(top = 4.dp, start = 16.dp, end = 16.dp)
        )

        OutlinedTextField(
            value = state.delayBetweenActions,
            onValueChange = onDelayBetweenActionsChanged,
            textStyle = AppTextStyles.editor,
            label = { Text("Delays between actions") },
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                .defaultMinSize(minWidth = 400.dp)
        )

        Text(
            text = "Delay in milliseconds to wait between autotype actions.",
            style = AppTextStyles.hint,
            modifier = Modifier
                .padding(top = 4.dp, start = 16.dp, end = 16.dp)
        )

        OutlinedTextField(
            value = state.command,
            onValueChange = onCommandChanged,
            textStyle = AppTextStyles.editor,
            label = { Text("Key transformation command") },
            modifier = Modifier
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                .defaultMinSize(minWidth = 400.dp)
        )

        Row {
            Text(
                text = "Shell command to transform key file",
                style = AppTextStyles.secondary,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(top = 4.dp, start = 16.dp)
            )

            Image(
                painter = painterResource("images/info_24.svg"),
                colorFilter = ColorFilter.tint(AppColors.icon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(28.dp)
                    .padding(start = 4.dp, top = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false),
                        onClick = onCommandInfoIconClicked
                    )
            )
        }

        if (state.availableAutotypeTypes.size > 1) {
            AutotypeSelectorView(
                selectedAutotypeType = state.selectedAutotypeType,
                availableAutotypeTypes = state.availableAutotypeTypes,
                onAutotypeChanged = onAutotypeChanged,
                modifier = Modifier
                    .padding(top = 24.dp)
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    val state = SettingsScreenState.Data(
        filePath = "/home/user/keepass.kdbx",
        keyPath = "/home/user/key",
        delay = "3000",
        delayBetweenActions = "200",
        command = "gpg --passphrase ABC123 --pinentry-mode loopback",
        selectedAutotypeType = AutotypeExecutorType.XDOTOOL,
        availableAutotypeTypes = listOf(
            AutotypeExecutorType.XDOTOOL,
            AutotypeExecutorType.OSA_SCRIPT
        )
    )

    PreviewWithBackground {
        ScreenContent(
            state = state,
            onCancelButtonClicked = {},
            onSaveButtonClicked = {},
            onFilePathChanged = {},
            onKeyPathChanged = {},
            onDelayChanged = {},
            onDelayBetweenActionsChanged = {},
            onCommandChanged = {},
            onAutotypeChanged = {}
        )
    }
}