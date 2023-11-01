package com.github.ai.autokpass.presentation.ui.screens.selectPattern

import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.extensions.ensureInRange
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.core.CoroutineViewModel
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.screens.autotype.AutotypeArgs
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.model.SearchItem
import com.github.ai.autokpass.util.StringUtils.EMPTY
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

class SelectPatternViewModel(
    private val interactor: SelectPatternInteractor,
    dispatchers: Dispatchers,
    private val strings: StringResources,
    private val router: Router,
    private val args: SelectPatternArgs
) : CoroutineViewModel(dispatchers) {

    private var patterns: List<AutotypePattern>? = null
    private var titles: List<String>? = null
    private var filteredItems: List<SearchItem>? = null

    private val intents = Channel<SelectPatternIntent>()

    private val _state = MutableStateFlow<SelectPatternState>(SelectPatternState.Loading)
    val state: StateFlow<SelectPatternState> = _state

    override fun start() {
        super.start()

        viewModelScope.launch {
            intents.receiveAsFlow()
                .onStart { emit(SelectPatternIntent.Init) }
                .flatMapLatest { intent -> handleIntent(intent, _state.value) }
                .collect { state ->
                    _state.value = state
                }
        }
    }

    fun sendIntent(intent: SelectPatternIntent) {
        if (intent.isImmediate) {
            handleIntent(intent, _state.value)
            return
        }

        viewModelScope.launch {
            intents.send(intent)
        }
    }

    private fun handleIntent(
        intent: SelectPatternIntent,
        state: SelectPatternState
    ): Flow<SelectPatternState> {
        return when (intent) {
            is SelectPatternIntent.Init -> loadData()

            is SelectPatternIntent.OnStartSearch -> search(intent.query)

            is SelectPatternIntent.OnQueryInputChanged -> {
                _state.value = getDataState()
                    .copy(
                        query = intent.query
                    )

                emptyFlow()
            }

            is SelectPatternIntent.OnUpKeyPressed -> {
                moveSelection(-1)
                emptyFlow()
            }

            is SelectPatternIntent.OnDownKeyPressed -> {
                moveSelection(1)
                emptyFlow()
            }

            is SelectPatternIntent.OnMouseClicked -> {
                navigateToAutotypeScreen(intent.index)
                emptyFlow()
            }

            is SelectPatternIntent.OnEnterPressed -> {
                navigateToAutotypeScreen(getDataState().selectedIndex)
                emptyFlow()
            }
        }
    }

    private fun moveSelection(delta: Int) {
        val currentState = getDataState()

        val newSelectedIndex = (currentState.selectedIndex + delta).ensureInRange(
            currentState.items.indices
        )

        _state.value = currentState.copy(
            selectedIndex = newSelectedIndex
        )
    }

    private fun navigateToAutotypeScreen(selectedIndex: Int) {
        val items = filteredItems ?: return
        val pattern = if (selectedIndex in items.indices) {
            items[selectedIndex].pattern
        } else {
            return
        }

        router.navigateTo(
            Screen.Autotype(
                args = AutotypeArgs(
                    entry = args.entry,
                    pattern = pattern
                )
            )
        )
    }

    private fun loadData(): Flow<SelectPatternState> {
        return flow {
            emit(SelectPatternState.Loading)

            val (patterns, titles) = interactor.loadData(args.entry)
            if (patterns.isEmpty()) {
                emit(
                    SelectPatternState.Empty(
                        message = strings.entryIsEmpty
                    )
                )
                return@flow
            }

            this@SelectPatternViewModel.patterns = patterns
            this@SelectPatternViewModel.titles = titles

            filteredItems = patterns.zip(titles)
                .map { (pattern, title) ->
                    SearchItem(
                        pattern = pattern,
                        text = title,
                        highlights = emptyList()
                    )
                }

            emit(
                SelectPatternState.Data(
                    query = EMPTY,
                    items = titles,
                    highlights = titles.map { emptyList() },
                    selectedIndex = 0
                )
            )
        }
    }

    private fun search(query: String): Flow<SelectPatternState> {
        val patterns = patterns ?: return emptyFlow()
        val titles = titles ?: return emptyFlow()

        return flow {
            val items = interactor.filter(
                query = query,
                patterns = patterns,
                titles = titles
            )

            val currentState = getDataState()

            filteredItems = items

            emit(
                SelectPatternState.Data(
                    query = currentState.query,
                    items = items.map { item -> item.text },
                    highlights = items.map { item -> item.highlights },
                    selectedIndex = currentState.selectedIndex.ensureInRange(items.indices)
                )
            )
        }
    }

    private fun getDataState(): SelectPatternState.Data {
        return _state.value as SelectPatternState.Data
    }

    sealed class SelectPatternState {

        object Loading : SelectPatternState()

        data class Data(
            val query: String,
            val items: List<String>,
            val highlights: List<List<Int>>,
            val selectedIndex: Int
        ) : SelectPatternState()

        data class Empty(
            val message: String
        ) : SelectPatternState()
    }

    sealed class SelectPatternIntent(
        val isImmediate: Boolean = false
    ) {

        object Init : SelectPatternIntent()

        data class OnQueryInputChanged(
            val query: String
        ) : SelectPatternIntent(isImmediate = true)

        data class OnStartSearch(
            val query: String
        ) : SelectPatternIntent()

        data class OnMouseClicked(
            val index: Int
        ) : SelectPatternIntent(isImmediate = true)

        object OnUpKeyPressed : SelectPatternIntent(isImmediate = true)

        object OnDownKeyPressed : SelectPatternIntent(isImmediate = true)

        object OnEnterPressed : SelectPatternIntent(isImmediate = true)
    }
}