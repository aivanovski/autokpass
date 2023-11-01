package com.github.ai.autokpass.presentation.ui.screens.selectPattern

import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.model.SearchItem

interface SelectPatternInteractor {

    suspend fun loadData(entry: KeepassEntry): Pair<List<AutotypePattern>, List<String>>

    suspend fun filter(
        query: String,
        patterns: List<AutotypePattern>,
        titles: List<String>
    ): List<SearchItem>
}