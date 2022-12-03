package com.github.ai.autokpass.presentation.ui.screens.select_entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.ai.autokpass.presentation.ui.core.CenteredBox
import com.github.ai.autokpass.presentation.ui.core.EmptyStateView
import com.github.ai.autokpass.presentation.ui.core.ErrorStateView
import com.github.ai.autokpass.presentation.ui.core.ProgressBar
import com.github.ai.autokpass.presentation.ui.core.SelectorView
import com.github.ai.autokpass.presentation.ui.screens.select_entry.SelectEntryViewModel.ScreenState

@Composable
fun SelectEntryScreen(viewModel: SelectEntryViewModel) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        with(state) {
            when (this) {
                is ScreenState.Loading -> {
                    CenteredBox { ProgressBar() }
                }

                is ScreenState.Error -> {
                    CenteredBox { ErrorStateView(message) }
                }

                is ScreenState.Empty -> {
                    CenteredBox { EmptyStateView(message) }
                }

                is ScreenState.Data -> {
                    SelectorView(
                        title = "Select entry",
                        query = query,
                        entries = entries.map { it.text },
                        highlights = entries.map { it.highlights },
                        selectedIndex = selectedIndex,
                        onInputTextChanged = { text -> viewModel.onQueryInputChanged(text) },
                        onItemClicked = { index -> viewModel.onItemClicked(index) },
                        onDownKeyPressed = { viewModel.moveSelectionDown() },
                        onUpKeyPressed = { viewModel.moveSelectionUp() },
                        onEnterKeyPressed = { viewModel.navigateToSelectPatternScreen() }
                    )
                }
            }
        }
    }
}

