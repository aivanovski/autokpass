package com.github.ai.autokpass.presentation.ui.screens.unlock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.extensions.collectAsStateImmediately
import com.github.ai.autokpass.presentation.ui.core.CenteredBox
import com.github.ai.autokpass.presentation.ui.core.ProgressBar
import com.github.ai.autokpass.presentation.ui.core.TextFieldIcons
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.core.theme.AppTextStyles
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockViewModel.ScreenState
import com.github.ai.autokpass.util.StringUtils.EMPTY

@Composable
fun UnlockScreen(viewModel: UnlockViewModel) {
    val state by viewModel.state.collectAsStateImmediately()

    Box(modifier = Modifier.fillMaxSize()) {
        with(state) {
            when (this) {
                is ScreenState.Loading -> {
                    CenteredBox { ProgressBar() }
                }

                is ScreenState.Data -> {
                    ScreenContent(
                        state = this,
                        onInputTextChanged = { text -> viewModel.onPasswordInputChanged(text) },
                        onUnlockButtonClicked = { viewModel.unlockDatabase() },
                        onPasswordIconClicked = { viewModel.togglePasswordVisibility() },
                        onErrorIconClicked = { viewModel.clearError() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ScreenContent(
    state: ScreenState.Data,
    onInputTextChanged: (text: String) -> Unit,
    onUnlockButtonClicked: () -> Unit,
    onPasswordIconClicked: () -> Unit,
    onErrorIconClicked: () -> Unit
) {
    val isError = (state.error != null)
    val focusRequester = remember { FocusRequester() }
    val strings: StringResources = get()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(fraction = 0.5f)
        ) {
            OutlinedTextField(
                value = state.password,
                singleLine = true,
                isError = isError,
                textStyle = AppTextStyles.editor,
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                onValueChange = onInputTextChanged,
                label = {
                    Text(
                        text = strings.password
                    )
                },
                trailingIcon = {
                    TextFieldIcons(
                        isPasswordToggleEnabled = true,
                        isError = isError,
                        isPasswordVisible = state.isPasswordVisible,
                        onErrorIconClicked = onErrorIconClicked,
                        onPasswordIconClicked = onPasswordIconClicked
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
                            onUnlockButtonClicked.invoke()
                            true
                        } else {
                            false
                        }
                    }
            )

            if (isError) {
                Text(
                    text = state.error ?: EMPTY,
                    style = AppTextStyles.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                onClick = onUnlockButtonClicked
            ) {
                Text(
                    text = strings.unlock,
                    style = AppTextStyles.button
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}