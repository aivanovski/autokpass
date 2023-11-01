package com.github.ai.autokpass.presentation.ui.screens.selectEntry

import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.ENTRIES
import com.github.ai.autokpass.TestData.ENTRY1
import com.github.ai.autokpass.TestData.EXCEPTION
import com.github.ai.autokpass.domain.MockErrorInteractorImpl
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.core.navigation.MockRouterImpl
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.MoveSelectionDown
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.MoveSelectionUp
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.OnEnterClicked
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.OnItemSelected
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.OnQueryInputChanged
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryIntent.OnStartSearch
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel.SelectEntryState
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.model.SearchItem
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternArgs
import com.github.ai.autokpass.util.StringUtils.EMPTY
import com.github.ai.autokpass.utils.TestDispatchers
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SelectEntryViewModelTest {

    private val strings = StringResourcesImpl()
    private val router = MockRouterImpl()

    @Nested
    @DisplayName("when init ")
    inner class WhenInit {

        @Test
        fun `should set Loading state`() {
            newViewModel().state.value shouldBe SelectEntryState.Loading
        }
    }

    @Nested
    @DisplayName("when start")
    inner class WhenStart {

        @Test
        fun `should show data`() {
            // arrange
            val viewModel = newViewModel()

            // act
            viewModel.start()

            // assert
            viewModel.state.value shouldBe SelectEntryState.Data(
                query = EMPTY,
                entries = ENTRIES.toSearchItems(),
                selectedIndex = 0
            )
        }

        @Test
        fun `should show empty state`() {
            // arrange
            val viewModel = newViewModel(
                interactor = MockSelectEntryInteractor(
                    entries = emptyList()
                )
            )

            // act
            viewModel.start()

            // assert
            viewModel.state.value shouldBe SelectEntryState.Empty(
                message = strings.noEntriesInDatabase
            )
        }

        @Test
        fun `should show error`() {
            // arrange
            val error = Result.Error(EXCEPTION)
            val viewModel = newViewModel(
                interactor = MockSelectEntryInteractor(
                    loadError = error
                )
            )

            // act
            viewModel.start()

            // assert
            viewModel.state.value shouldBe SelectEntryState.Error(
                message = EXCEPTION.message ?: EMPTY
            )
        }
    }

    @Nested
    @DisplayName("when query input changed")
    inner class WhenQueryInputChanged {

        @Test
        fun `should filter entries`() {
            // arrange
            val viewModel = newViewModel()
            val query = ENTRY1.title
            viewModel.setupState(
                SelectEntryState.Data(
                    query = EMPTY,
                    entries = ENTRIES.toSearchItems(),
                    selectedIndex = 0
                )
            )

            // act
            viewModel.sendIntent(OnQueryInputChanged(query))
            viewModel.sendIntent(OnStartSearch(query))

            // assert
            viewModel.state.value shouldBe SelectEntryState.Data(
                query = query,
                entries = listOf(ENTRIES[0]).toSearchItems(),
                selectedIndex = 0
            )
        }

        @Test
        fun `should show error`() {
            // arrange
            val error = Result.Error(EXCEPTION)
            val query = ENTRY1.title
            val viewModel = newViewModel(
                interactor = MockSelectEntryInteractor(
                    entries = ENTRIES,
                    filterError = error
                )
            )
            viewModel.setupState(
                SelectEntryState.Data(
                    query = EMPTY,
                    entries = ENTRIES.toSearchItems(),
                    selectedIndex = 0
                )
            )

            // act
            viewModel.sendIntent(OnQueryInputChanged(query))
            viewModel.sendIntent(OnStartSearch(query))

            // assert
            viewModel.state.value shouldBe SelectEntryState.Error(
                message = EXCEPTION.message ?: EMPTY
            )
        }
    }

    @Nested
    @DisplayName("when key pressed")
    inner class WhenKeyPressed {

        @Test
        fun `should move cursor down`() {
            // arrange
            val viewModel = newViewModel()
            viewModel.setupState(
                SelectEntryState.Data(
                    query = EMPTY,
                    entries = ENTRIES.toSearchItems(),
                    selectedIndex = 0
                )
            )

            // act
            viewModel.sendIntent(MoveSelectionDown)

            // assert
            viewModel.state.value shouldBe SelectEntryState.Data(
                query = EMPTY,
                entries = ENTRIES.toSearchItems(),
                selectedIndex = 1
            )
        }

        @Test
        fun `should move cursor up`() {
            // arrange
            val viewModel = newViewModel()
            viewModel.setupState(
                SelectEntryState.Data(
                    query = EMPTY,
                    entries = ENTRIES.toSearchItems(),
                    selectedIndex = 1
                )
            )

            // act
            viewModel.sendIntent(MoveSelectionUp)

            // assert
            viewModel.state.value shouldBe SelectEntryState.Data(
                query = EMPTY,
                entries = ENTRIES.toSearchItems(),
                selectedIndex = 0
            )
        }

        @Test
        fun `should navigate to next screen`() {
            // arrange
            val viewModel = newViewModel()
            viewModel.setupState(
                SelectEntryState.Data(
                    query = EMPTY,
                    entries = ENTRIES.toSearchItems(),
                    selectedIndex = 0
                )
            )

            // act
            viewModel.sendIntent(OnEnterClicked)

            // assert
            router.lastScreen.shouldBe(
                Screen.SelectPattern(
                    args = SelectPatternArgs(ENTRY1)
                )
            )
            viewModel.state.value shouldBe SelectEntryState.Data(
                query = EMPTY,
                entries = ENTRIES.toSearchItems(),
                selectedIndex = 0
            )
        }
    }

    @Nested
    @DisplayName("when mouse clicked")
    inner class WhenMouseClicked {

        @Test
        fun `should navigate to next screen`() {
            // arrange
            val viewModel = newViewModel()
            val selectedIndex = 1
            viewModel.setupState(
                SelectEntryState.Data(
                    query = EMPTY,
                    entries = ENTRIES.toSearchItems(),
                    selectedIndex = 0
                )
            )

            // act
            viewModel.sendIntent(OnItemSelected(selectedIndex))

            // assert
            router.lastScreen.shouldBe(
                Screen.SelectPattern(
                    args = SelectPatternArgs(ENTRIES[selectedIndex])
                )
            )
            viewModel.state.value shouldBe SelectEntryState.Data(
                query = EMPTY,
                entries = ENTRIES.toSearchItems(),
                selectedIndex = 0
            )
        }
    }

    private fun SelectEntryViewModel.setupState(
        state: SelectEntryState
    ) {
        this.start()
        (this.state as MutableStateFlow).value = state
    }

    private fun newArgs(): SelectEntryArgs =
        SelectEntryArgs(
            key = KeepassKey.PasswordKey(DB_PASSWORD),
            filePath = DB_PATH
        )

    private fun newViewModel(
        interactor: SelectEntryInteractor = MockSelectEntryInteractor(ENTRIES)
    ): SelectEntryViewModel {
        return SelectEntryViewModel(
            interactor = interactor,
            errorInteractor = MockErrorInteractorImpl(),
            dispatchers = TestDispatchers(),
            strings = strings,
            router = router,
            args = newArgs()
        )
    }

    private fun List<KeepassEntry>.toSearchItems(): List<SearchItem> {
        return this.map { entry ->
            SearchItem(
                entry = entry,
                text = entry.title,
                highlights = emptyList()
            )
        }
    }
}