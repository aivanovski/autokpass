package com.github.ai.autokpass.presentation.ui.screens.selectPattern

import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.model.SearchItem

class MockSelectPatternInteractorImpl(
    private val onLoadAll: () -> Pair<List<AutotypePattern>, List<String>>,
    private val onFilter: (query: String) -> List<SearchItem>
) : SelectPatternInteractor {

    override suspend fun loadData(entry: KeepassEntry): Pair<List<AutotypePattern>, List<String>> {
        return onLoadAll.invoke()
    }

    override suspend fun filter(
        query: String,
        patterns: List<AutotypePattern>,
        titles: List<String>
    ): List<SearchItem> {
        return onFilter.invoke(query)
    }
}