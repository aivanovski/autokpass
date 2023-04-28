package com.github.ai.autokpass.presentation.ui.screens.selectPattern

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.extensions.collectAsStateImmediately
import com.github.ai.autokpass.presentation.ui.core.SelectorView
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.ScreenState

@Composable
fun SelectPatternScreen(viewModel: SelectPatternViewModel) {
    val state by viewModel.state.collectAsStateImmediately()
    val strings: StringResources = get()

    Box(modifier = Modifier.fillMaxSize()) {
        with(state) {
            when (this) {
                is ScreenState.Data -> {
                    SelectorView(
                        title = strings.selectPattern,
                        query = query,
                        entries = items,
                        highlights = highlights,
                        selectedIndex = selectedIndex,
                        onInputTextChanged = { text -> viewModel.onQueryInputChanged(text) },
                        onItemClicked = { index -> viewModel.onItemClicked(index) },
                        onDownKeyPressed = { viewModel.moveSelectionDown() },
                        onUpKeyPressed = { viewModel.moveSelectionUp() },
                        onEnterKeyPressed = { viewModel.navigateToAutotypeScreen() }
                    )
                }
            }
        }
    }
}