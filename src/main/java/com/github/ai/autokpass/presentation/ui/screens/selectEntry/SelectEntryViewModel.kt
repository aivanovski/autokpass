package com.github.ai.autokpass.presentation.ui.screens.selectEntry

import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.model.SearchItem
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternArgs
import com.github.ai.autokpass.util.StringUtils.EMPTY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SelectEntryViewModel(
    private val interactor: SelectEntryInteractor,
    private val errorInteractor: ErrorInteractor,
    dispatchers: Dispatchers,
    private val strings: StringResources,
    private val router: Router,
    private val args: SelectEntryArgs
) : CoroutineViewModel(dispatchers) {

    private val intents = Channel<SelectEntryIntent>()

    private val _state = MutableStateFlow<SelectEntryState>(SelectEntryState.Loading)
    val state: StateFlow<SelectEntryState> = _state

    private var allEntries: List<KeepassEntry>? = null
    private var allTitles: List<String>? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun start() {
        super.start()

        viewModelScope.launch {
            intents.receiveAsFlow()
                .onStart { emit(SelectEntryIntent.Init) }
                .flatMapLatest { intent -> handleIntent(intent, _state.value) }
                .collect { state ->
                    _state.value = state
                }
        }
    }

    fun sendIntent(intent: SelectEntryIntent) {
        if (intent.isImmediate) {
            handleIntent(intent, _state.value)
            return
        }

        viewModelScope.launch {
            intents.send(intent)
        }
    }

    private fun handleIntent(
        intent: SelectEntryIntent,
        state: SelectEntryState
    ): Flow<SelectEntryState> {
        return when (intent) {
            is SelectEntryIntent.Init -> loadData()

            is SelectEntryIntent.OnStartSearch -> {
                search(intent.query)
            }

            is SelectEntryIntent.OnQueryInputChanged -> {
                _state.value = getDataState()
                    .copy(
                        query = intent.newQuery
                    )

                emptyFlow()
            }

            is SelectEntryIntent.MoveSelectionUp -> {
                moveSelection(-1)
                emptyFlow()
            }

            is SelectEntryIntent.MoveSelectionDown -> {
                moveSelection(1)
                emptyFlow()
            }

            is SelectEntryIntent.OnItemSelected -> {
                navigateToSelectPatternScreen(intent.index)
                emptyFlow()
            }

            is SelectEntryIntent.OnEnterClicked -> {
                val selectedIndex = getDataState().selectedIndex
                navigateToSelectPatternScreen(selectedIndex)
                emptyFlow()
            }
        }
    }

    private fun moveSelection(delta: Int) {
        val currentState = getDataState()

        _state.value = currentState.copy(
            selectedIndex = ensureSelectedIndexCorrect(
                currentState.selectedIndex + delta,
                currentState.entries
            )
        )
    }

    private fun loadData(): Flow<SelectEntryState> {
        return flow {
            emit(SelectEntryState.Loading)

            val getEntriesResult = interactor.loadAllEntries(
                key = args.key,
                filePath = args.filePath
            )
            if (getEntriesResult.isFailed()) {
                emit(newErrorState(getEntriesResult.asErrorOrThrow()))
                return@flow
            }

            val data = getEntriesResult.getDataOrThrow()
            val allEntries = data.first
                .also {
                    allEntries = it
                }
            val allTitles = data.second
                .also {
                    allTitles = it
                }

            val filteredEntries = allEntries.zip(allTitles)
                .map { (entry, title) ->
                    SearchItem(
                        entry = entry,
                        text = title,
                        highlights = emptyList()
                    )
                }

            if (allEntries.isNotEmpty()) {
                emit(
                    SelectEntryState.Data(
                        query = EMPTY,
                        entries = filteredEntries,
                        selectedIndex = 0
                    )
                )
            } else {
                emit(SelectEntryState.Empty(strings.noEntriesInDatabase))
            }
        }
    }

    private fun search(query: String): Flow<SelectEntryState> {
        val allEntries = allEntries ?: return emptyFlow()
        val allTitles = allTitles ?: return emptyFlow()

        return flow {
            val filterEntriesResult = interactor.filterEntries(
                allEntries = allEntries,
                allTitles = allTitles,
                query = query
            )
            if (!filterEntriesResult.isSucceeded()) {
                emit(newErrorState(filterEntriesResult.asErrorOrThrow()))
                return@flow
            }

            val newEntries = filterEntriesResult.getDataOrThrow()

            val currentState = (_state.value as? SelectEntryState.Data)
            val selectedIndex = ensureSelectedIndexCorrect(
                currentState?.selectedIndex ?: 0,
                newEntries
            )

            emit(
                SelectEntryState.Data(
                    query = currentState?.query ?: query,
                    entries = newEntries,
                    selectedIndex = selectedIndex
                )
            )
        }
    }

    private fun navigateToSelectPatternScreen(entryIndex: Int) {
        val entries = getDataState().entries

        if (entryIndex < entries.size) {
            router.navigateTo(
                Screen.SelectPattern(
                    args = SelectPatternArgs(
                        entries[entryIndex].entry
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

    private fun getDataState(): SelectEntryState.Data {
        return (_state.value as SelectEntryState.Data)
    }

    private fun newErrorState(error: Result.Error): SelectEntryState =
        SelectEntryState.Error(
            message = errorInteractor.processAndGetMessage(error)
        )

    sealed class SelectEntryState {

        object Loading : SelectEntryState()

        data class Error(val message: String) : SelectEntryState()

        data class Empty(val message: String) : SelectEntryState()

        data class Data(
            val query: String,
            val entries: List<SearchItem>,
            val selectedIndex: Int
        ) : SelectEntryState()
    }

    sealed class SelectEntryIntent(
        val isImmediate: Boolean = false
    ) {
        object Init : SelectEntryIntent()

        data class OnQueryInputChanged(
            val newQuery: String
        ) : SelectEntryIntent(isImmediate = true)

        data class OnStartSearch(
            val query: String
        ) : SelectEntryIntent()

        object MoveSelectionDown : SelectEntryIntent(isImmediate = true)

        object MoveSelectionUp : SelectEntryIntent(isImmediate = true)

        data class OnItemSelected(
            val index: Int
        ) : SelectEntryIntent(isImmediate = true)

        object OnEnterClicked : SelectEntryIntent(isImmediate = true)
    }
}