package com.github.ai.autokpass.presentation.ui.screens.selectPattern

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.extensions.collectAsStateImmediately
import com.github.ai.autokpass.presentation.ui.core.CenteredBox
import com.github.ai.autokpass.presentation.ui.core.EmptyStateView
import com.github.ai.autokpass.presentation.ui.core.ProgressBar
import com.github.ai.autokpass.presentation.ui.core.SelectorView
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnDownKeyPressed
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnEnterPressed
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnMouseClicked
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnQueryInputChanged
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnStartSearch
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnUpKeyPressed
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternState

@Composable
fun SelectPatternScreen(viewModel: SelectPatternViewModel) {
    val state by viewModel.state.collectAsStateImmediately()
    val strings: StringResources = get()

    Box(modifier = Modifier.fillMaxSize()) {
        with(state) {
            when (this) {
                is SelectPatternState.Loading -> {
                    CenteredBox { ProgressBar() }
                }

                is SelectPatternState.Empty -> {
                    CenteredBox { EmptyStateView(message) }
                }

                is SelectPatternState.Data -> {
                    SelectorView(
                        title = strings.selectPattern,
                        query = query,
                        entries = items,
                        highlights = highlights,
                        selectedIndex = selectedIndex,
                        onInputTextChanged = { text ->
                            viewModel.sendIntent(OnQueryInputChanged(text))
                            viewModel.sendIntent(OnStartSearch(text))
                        },
                        onItemClicked = { index -> viewModel.sendIntent(OnMouseClicked(index)) },
                        onDownKeyPressed = { viewModel.sendIntent(OnDownKeyPressed) },
                        onUpKeyPressed = { viewModel.sendIntent(OnUpKeyPressed) },
                        onEnterKeyPressed = { viewModel.sendIntent(OnEnterPressed) }
                    )
                }
            }
        }
    }
}