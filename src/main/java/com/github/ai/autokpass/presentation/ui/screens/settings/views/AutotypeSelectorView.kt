package com.github.ai.autokpass.presentation.ui.screens.settings.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.presentation.ui.core.PreviewWithBackground
import com.github.ai.autokpass.presentation.ui.core.theme.AppTextStyles

@Composable
fun AutotypeSelectorView(
    selectedAutotypeType: AutotypeExecutorType,
    availableAutotypeTypes: List<AutotypeExecutorType>,
    onAutotypeChanged: (type: AutotypeExecutorType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Autotype executor:",
            style = AppTextStyles.primary,
            modifier = Modifier
                .padding(start = 16.dp)
        )

        for (autotypeType in availableAutotypeTypes) {
            Row {
                RadioButton(
                    selected = (selectedAutotypeType == autotypeType),
                    onClick = { onAutotypeChanged(autotypeType) },
                )

                Text(
                    text = autotypeType.cliName,
                    style = AppTextStyles.primary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Preview
@Composable
fun AutotypeSelectorPreview() {
    PreviewWithBackground {
        AutotypeSelectorView(
            selectedAutotypeType = AutotypeExecutorType.OSA_SCRIPT,
            availableAutotypeTypes = listOf(
                AutotypeExecutorType.OSA_SCRIPT,
                AutotypeExecutorType.CLICLICK
            ),
            onAutotypeChanged = {}
        )
    }
}