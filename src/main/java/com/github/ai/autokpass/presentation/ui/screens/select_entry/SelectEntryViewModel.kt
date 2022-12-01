package com.github.ai.autokpass.presentation.ui.screens.select_entry

import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.screens.select_entry.model.SearchItem
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.screens.select_pattern.SelectPatternArgs
import com.github.ai.autokpass.util.StringUtils.EMPTY
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SelectEntryViewModel(
    private val interactor: SelectEntryInteractor,
    private val errorInteractor: ErrorInteractor,
    dispatchers: Dispatchers,
    private val router: Router,
    private val args: SelectEntryArgs,
    private val appArgs: ParsedArgs
) : CoroutineViewModel(dispatchers) {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val state: StateFlow<ScreenState> = _state

    private var searchJob: Job? = null

    private var allEntries: List<KeepassEntry>? = null
    private var allTitles: List<String>? = null
    private var filteredEntries: List<SearchItem>? = null
    private var query = EMPTY
    private var selectedIndex = 0

    override fun start() {
        super.start()

        _state.value = ScreenState.Loading

        viewModelScope.launch {
            val getEntriesResult = interactor.loadAll(
                key = args.key,
                filePath = appArgs.filePath
            )
            if (getEntriesResult.isSucceeded()) {
                val data = getEntriesResult.getDataOrThrow()
                val allEntries = data.first
                    .also {
                        allEntries = it
                    }
                val allTitles = data.second
                    .also {
                        allTitles = it
                    }

                filteredEntries = allEntries.zip(allTitles)
                    .map { (entry, title) ->
                        SearchItem(
                            entry = entry,
                            text = title,
                            highlights = emptyList()
                        )
                    }

                if (allEntries.isNotEmpty()) {
                    _state.value = ScreenState.Data(
                        query = query,
                        entries = filteredEntries ?: emptyList(),
                        selectedIndex = selectedIndex
                    )
                } else {
                    _state.value = ScreenState.Empty("No entries in database")
                }
            } else {
                showError(getEntriesResult.asErrorOrThrow())
            }
        }
    }

    override fun stop() {
        super.stop()
        searchJob?.cancel()
    }

    private fun showError(error: Result.Error) {
        _state.value = ScreenState.Error(
            message = errorInteractor.processAndGetMessage(error)
        )
    }

    private fun search() {
        val allEntries = allEntries ?: return
        val allTitles = allTitles ?: return

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            val filterEntriesResult = interactor.filter(
                allEntries = allEntries,
                allTitles = allTitles,
                query = query,
            )

            if (filterEntriesResult.isSucceeded()) {
                val newEntries = filterEntriesResult.getDataOrThrow()
                filteredEntries = newEntries

                selectedIndex = ensureSelectedIndexCorrect(selectedIndex, newEntries)

                _state.value = ScreenState.Data(
                    query = query,
                    entries = filteredEntries ?: emptyList(),
                    selectedIndex = selectedIndex
                )

                searchJob = null
            } else {
                showError(filterEntriesResult.asErrorOrThrow())
            }
        }
    }

    fun onQueryInputChanged(newQuery: String) {
        query = newQuery

        val currentState = (_state.value as? ScreenState.Data) ?: return

        _state.value = currentState.copy(
            query = newQuery
        )

        if (allEntries != null) {
            search()
        }
    }

    fun onItemClicked(itemIndex: Int) {
        selectedIndex = itemIndex

        navigateToSelectPatternScreen()
    }

    fun moveSelectionDown() {
        val currentState = (_state.value as? ScreenState.Data) ?: return
        val entries = currentState.entries

        selectedIndex = ensureSelectedIndexCorrect(selectedIndex + 1, entries)

        _state.value = currentState.copy(
            selectedIndex = selectedIndex
        )
    }

    fun moveSelectionUp() {
        val currentState = (_state.value as? ScreenState.Data) ?: return
        val entries = currentState.entries

        selectedIndex = ensureSelectedIndexCorrect(selectedIndex - 1, entries)

        _state.value = currentState.copy(
            selectedIndex = selectedIndex
        )
    }

    fun navigateToSelectPatternScreen() {
        val entries = filteredEntries ?: return

        if (selectedIndex < entries.size) {
            router.navigateTo(
                Screen.SelectPattern(
                    args = SelectPatternArgs(
                        entries[selectedIndex].entry
                    )
                )
            )
        }
    }

    private fun ensureSelectedIndexCorrect(
        selectedIndex: Int,
        entries: List<Any>
    ): Int {
        return when {
            selectedIndex < 0 -> 0
            selectedIndex >= entries.size -> 0.coerceAtLeast(entries.size - 1)
            else -> selectedIndex
        }
    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Error(val message: String) : ScreenState()

        data class Empty(val message: String) : ScreenState()

        data class Data(
            val query: String,
            val entries: List<SearchItem>,
            val selectedIndex: Int
        ) : ScreenState()
    }
}