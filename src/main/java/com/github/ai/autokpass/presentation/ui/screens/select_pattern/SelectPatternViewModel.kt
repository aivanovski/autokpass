package com.github.ai.autokpass.presentation.ui.screens.select_pattern

import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.extensions.ensureInRange
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.screens.select_pattern.model.SearchItem
import com.github.ai.autokpass.util.StringUtils.EMPTY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SelectPatternViewModel(
    private val interactor: SelectPatternInteractor,
    dispatchers: Dispatchers,
    private val router: Router,
    private val args: SelectPatternArgs,
    private val appArgs: ParsedArgs
) : CoroutineViewModel(dispatchers) {

    private var query = EMPTY
    private var selectedIndex = 0

    private var allPatterns: List<AutotypePattern>? = null
    private var allTitles: List<String>? = null
    private var filteredItems: List<SearchItem>? = null

    private val _state = MutableStateFlow<ScreenState>(createDataState())
    val state: StateFlow<ScreenState> = _state

    override fun start() {
        super.start()
        loadData()
    }

    fun onItemClicked(index: Int) {
        selectedIndex = index

        navigateToAutotypeScreen()
    }

    fun navigateToAutotypeScreen() {
        val items = filteredItems ?: return

        if (selectedIndex !in items.indices) {
            return
        }

        throw NotImplementedError("Navigate to Autotype screen")
    }

    fun onQueryInputChanged(newQuery: String) {
        query = newQuery
        updateScreenState()

        search()
    }

    fun moveSelectionDown() {
        val items = filteredItems ?: return

        selectedIndex = (selectedIndex + 1).ensureInRange(items.indices)

        updateScreenState()
    }

    fun moveSelectionUp() {
        val items = filteredItems ?: return

        selectedIndex = (selectedIndex - 1).ensureInRange(items.indices)

        updateScreenState()
    }

    private fun loadData() {
        viewModelScope.launch {
            val (patterns, titles) = interactor.loadAll()

            allPatterns = patterns
            allTitles = titles
            filteredItems = patterns.zip(titles)
                .map { (pattern, title) ->
                    SearchItem(
                        pattern = pattern,
                        text = title,
                        highlights = emptyList()
                    )
                }

            updateScreenState()
        }
    }

    private fun search() {
        val allPatterns = allPatterns ?: return
        val allTitles = allTitles ?: return

        viewModelScope.launch {
            val items = interactor.filter(
                query = query,
                patterns = allPatterns,
                titles = allTitles
            )

            filteredItems = items
            selectedIndex = selectedIndex.ensureInRange(items.indices)

            updateScreenState()
        }
    }

    private fun updateScreenState() {
        _state.value = createDataState()
    }

    private fun createDataState(): ScreenState.Data {
        return ScreenState.Data(
            query = query,
            items = filteredItems?.map { it.text } ?: emptyList(),
            highlights = filteredItems?.map { it.highlights } ?: emptyList(),
            selectedIndex = selectedIndex
        )
    }

    sealed class ScreenState {
        data class Data(
            val query: String,
            val items: List<String>,
            val highlights: List<List<Int>>,
            val selectedIndex: Int
        ) : ScreenState()
    }
}