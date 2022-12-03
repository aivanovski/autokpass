package com.github.ai.autokpass.presentation.ui.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.ai.autokpass.presentation.ui.core.theme.AppColors
import com.github.ai.autokpass.presentation.ui.core.theme.AppTextStyles

@Composable
fun CenteredColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

@Composable
fun CenteredBox(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun ProgressBar() {
    CircularProgressIndicator()
}

@Composable
fun EmptyStateView(message: String) {
    Text(
        text = message
    )
}

@Composable
fun ErrorStateView(message: String) {
    Text(
        text = message,
        style = AppTextStyles.error,
        color = AppColors.materialColors.error
    )
}