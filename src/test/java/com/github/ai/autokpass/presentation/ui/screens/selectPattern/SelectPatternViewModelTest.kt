package com.github.ai.autokpass.presentation.ui.screens.selectPattern

import com.github.ai.autokpass.TestData.EMPTY_ENTRY
import com.github.ai.autokpass.TestData.ENTRY1
import com.github.ai.autokpass.domain.autotype.AutotypePatternFactory
import com.github.ai.autokpass.domain.autotype.AutotypePatternFormatter
import com.github.ai.autokpass.domain.fuzzySearch.Fzf4jFuzzyMatcher
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.core.navigation.MockRouterImpl
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import com.github.ai.autokpass.presentation.ui.screens.autotype.AutotypeArgs
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnDownKeyPressed
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnEnterPressed
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnMouseClicked
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnQueryInputChanged
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnStartSearch
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternIntent.OnUpKeyPressed
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel.SelectPatternState
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.model.SearchItem
import com.github.ai.autokpass.util.StringUtils.EMPTY
import com.github.ai.autokpass.utils.TestDispatchers
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SelectPatternViewModelTest {

    private val factory = AutotypePatternFactory()
    private val strings = StringResourcesImpl()
    private val router = MockRouterImpl()

    @Nested
    @DisplayName("when init")
    inner class WhenInit {

        @Test
        fun `should set Loading state`() {
            newViewModel(ENTRY1).state.value shouldBe SelectPatternState.Loading
        }
    }

    @Nested
    @DisplayName("when start")
    inner class WhenStart {

        @Test
        fun `should show data`() {
            // arrange
            val viewModel = newViewModel(ENTRY1)
            val patterns = factory.createPatternsForEntry(ENTRY1)

            // act
            viewModel.start()

            // assert
            viewModel.state.value shouldBe SelectPatternState.Data(
                query = EMPTY,
                items = patterns.formatToText(),
                highlights = PATTERNS.map { emptyList() },
                selectedIndex = 0
            )
        }

        @Test
        fun `should show empty state`() {
            // arrange
            val viewModel = newViewModel(
                entry = EMPTY_ENTRY,
                interactor = newMockInteractor(
                    onLoadAll = { emptyList<AutotypePattern>() to emptyList() }
                )
            )

            // act
            viewModel.start()

            // assert
            viewModel.state.value shouldBe SelectPatternState.Empty(
                message = strings.entryIsEmpty
            )
        }
    }

    @Nested
    @DisplayName("when query input changed")
    inner class WhenQueryInputChanged {

        @Test
        fun `should filter patterns`() {
            // arrange
            val viewModel = newViewModel(
                entry = ENTRY1,
                interactor = newInteractor()
            )
            val titles = PATTERNS.formatToText()
            val query = titles.first()
            viewModel.setupState(DEFAULT_DATA_STATE)

            // act
            viewModel.sendIntent(OnQueryInputChanged(query))
            viewModel.sendIntent(OnStartSearch(query))

            // assert
            viewModel.state.value shouldBe SelectPatternState.Data(
                query = query,
                items = listOf(titles.first()),
                highlights = listOf(query.indices.toList()),
                selectedIndex = 0
            )
        }
    }

    @Nested
    @DisplayName("when key pressed")
    inner class WhenKeyPressed {

        @Test
        fun `should move cursor down`() {
            // arrange
            val viewModel = newViewModel(ENTRY1)
            viewModel.setupState(
                state = DEFAULT_DATA_STATE.copy(
                    selectedIndex = 0
                )
            )

            // act
            viewModel.sendIntent(OnDownKeyPressed)

            // assert
            with(viewModel.state.value) {
                shouldBeInstanceOf<SelectPatternState.Data>()
                selectedIndex shouldBe 1
            }
        }

        @Test
        fun `should move cursor up`() {
            // arrange
            val viewModel = newViewModel(ENTRY1)
            viewModel.setupState(
                state = DEFAULT_DATA_STATE.copy(
                    selectedIndex = 1
                )
            )

            // act
            viewModel.sendIntent(OnUpKeyPressed)

            // assert
            with(viewModel.state.value) {
                shouldBeInstanceOf<SelectPatternState.Data>()
                selectedIndex shouldBe 0
            }
        }

        @Test
        fun `should navigate to next screen`() {
            // arrange
            val viewModel = newViewModel(ENTRY1)
            viewModel.setupState(DEFAULT_DATA_STATE)

            // act
            viewModel.sendIntent(OnEnterPressed)

            // assert
            router.lastScreen shouldBe Screen.Autotype(
                args = AutotypeArgs(
                    entry = ENTRY1,
                    pattern = PATTERNS.first()
                )
            )
            viewModel.state.value should beInstanceOf<SelectPatternState.Data>()
        }
    }

    @Nested
    @DisplayName("when mouse clicked")
    inner class WhenMouseClicked {

        @Test
        fun `should navigate to next screen`() {
            // arrange
            val viewModel = newViewModel(ENTRY1)
            val selectedIndex = 1
            viewModel.setupState(DEFAULT_DATA_STATE)

            // act
            viewModel.sendIntent(OnMouseClicked(selectedIndex))

            // assert
            router.lastScreen shouldBe Screen.Autotype(
                args = AutotypeArgs(
                    entry = ENTRY1,
                    pattern = PATTERNS[selectedIndex]
                )
            )
            viewModel.state.value should beInstanceOf<SelectPatternState.Data>()
        }

        @Test
        fun `should do nothing if index is invalid`() {
            // arrange
            val viewModel = newViewModel(ENTRY1)
            val selectedIndex = 100
            viewModel.setupState(DEFAULT_DATA_STATE)

            // act
            viewModel.sendIntent(OnMouseClicked(selectedIndex))

            // assert
            router.lastScreen shouldBe null
            viewModel.state.value should beInstanceOf<SelectPatternState.Data>()
        }
    }

    private fun SelectPatternViewModel.setupState(
        state: SelectPatternState
    ) {
        this.start()
        (this.state as MutableStateFlow).value = state
    }

    private fun List<AutotypePattern>.toSearchItems(): List<SearchItem> {
        val titles = this.formatToText()

        return (this.zip(titles)).map { (pattern, title) ->
            SearchItem(
                pattern = pattern,
                text = title,
                highlights = emptyList()
            )
        }
    }

    private fun newMockInteractor(
        onLoadAll: () -> Pair<List<AutotypePattern>, List<String>> = {
            PATTERNS to PATTERNS.formatToText()
        },
        onFilter: (query: String) -> List<SearchItem> = { PATTERNS.toSearchItems() }
    ): SelectPatternInteractor {
        return MockSelectPatternInteractorImpl(
            onLoadAll = onLoadAll,
            onFilter = onFilter
        )
    }

    private fun newInteractor(): SelectPatternInteractor =
        SelectPatternInteractorImpl(
            fuzzyMatcher = Fzf4jFuzzyMatcher(),
            formatter = AutotypePatternFormatter(),
            dispatchers = TestDispatchers(),
            factory = AutotypePatternFactory()
        )

    private fun newViewModel(
        entry: KeepassEntry,
        interactor: SelectPatternInteractor = newMockInteractor()
    ): SelectPatternViewModel =
        SelectPatternViewModel(
            interactor = interactor,
            dispatchers = TestDispatchers(),
            strings = strings,
            router = router,
            args = SelectPatternArgs(
                entry = entry
            )
        )

    companion object {
        private val PATTERNS = AutotypePattern.ALL

        private val DEFAULT_DATA_STATE = SelectPatternState.Data(
            query = EMPTY,
            items = PATTERNS.formatToText(),
            highlights = PATTERNS.map { emptyList() },
            selectedIndex = 0
        )

        private fun List<AutotypePattern>.formatToText(): List<String> {
            val formatter = AutotypePatternFormatter()

            return this.mapIndexed { index, pattern ->
                "${index + 1} ${formatter.format(pattern)}"
            }
        }
    }
}