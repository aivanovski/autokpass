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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.github.ai.autokpass.presentation.ui.core.ProgressBar
import com.github.ai.autokpass.presentation.ui.core.TextFieldIcons
import com.github.ai.autokpass.presentation.ui.core.theme.AppTextStyles
import com.github.ai.autokpass.util.StringUtils.EMPTY

@Composable
fun UnlockScreen(viewModel: UnlockViewModel) {
    val isLoading = viewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading.value) {
            ProgressBar()
        } else {
            ScreenContent(viewModel)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ScreenContent(viewModel: UnlockViewModel) {
    val password by viewModel.password.collectAsState()
    val error by viewModel.error.collectAsState()
    val isError = (error != null)
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val focusRequester = remember { FocusRequester() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(fraction = 0.5f)
        ) {
            OutlinedTextField(
                value = password,
                singleLine = true,
                isError = isError,
                textStyle = AppTextStyles.editor,
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                onValueChange = { text -> viewModel.onPasswordInputChanged(text) },
                label = {
                    Text(
                        text = "Password"
                    )
                },
                trailingIcon = {
                    TextFieldIcons(
                        isPasswordToggleEnabled = true,
                        isError = isError,
                        isPasswordVisible = isPasswordVisible,
                        onErrorIconClicked = { viewModel.removeError() },
                        onPasswordIconClicked = { viewModel.togglePasswordVisibility() }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
                            viewModel.unlockDatabase()
                            true
                        } else {
                            false
                        }
                    }
            )

            if (isError) {
                Text(
                    text = error ?: EMPTY,
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
                onClick = {
                    viewModel.unlockDatabase()
                }
            ) {
                Text(
                    text = "Unlock",
                    style = AppTextStyles.button
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

