package com.github.ai.autokpass.presentation.ui.screens.selectEntry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.extensions.collectAsStateImmediately
import com.github.ai.autokpass.presentation.ui.core.CenteredBox
import com.github.ai.autokpass.presentation.ui.core.EmptyStateView
import com.github.ai.autokpass.presentation.ui.core.ErrorStateView
import com.github.ai.autokpass.presentation.ui.core.ProgressBar
import com.github.ai.autokpass.presentation.ui.core.SelectorView
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.MoveSelectionDown
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.MoveSelectionUp
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.OnEnterClicked
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.OnItemSelected
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.OnQueryInputChanged
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.OnStartSearch
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryState

@Composable
fun SelectEntryScreen(viewModel: SelectEntryViewModel) {
    val state by viewModel.state.collectAsStateImmediately()
    val strings: StringResources = get()

    Box(modifier = Modifier.fillMaxSize()) {
        with(state) {
            when (this) {
                is SelectEntryState.Loading -> {
                    CenteredBox { ProgressBar() }
                }

                is SelectEntryState.Error -> {
                    CenteredBox { ErrorStateView(message) }
                }

                is SelectEntryState.Empty -> {
                    CenteredBox { EmptyStateView(message) }
                }

                is SelectEntryState.Data -> {
                    SelectorView(
                        title = strings.selectEntry,
                        query = query,
                        entries = entries.map { it.text },
                        highlights = entries.map { it.highlights },
                        selectedIndex = selectedIndex,
                        onInputTextChanged = { text ->
                            viewModel.sendIntent(OnQueryInputChanged(text))
                            viewModel.sendIntent(OnStartSearch(text))
                        },
                        onItemClicked = { index -> viewModel.sendIntent(OnItemSelected(index)) },
                        onDownKeyPressed = { viewModel.sendIntent(MoveSelectionDown) },
                        onUpKeyPressed = { viewModel.sendIntent(MoveSelectionUp) },
                        onEnterKeyPressed = { viewModel.sendIntent(OnEnterClicked) }
                    )
                }
            }
        }
    }
}