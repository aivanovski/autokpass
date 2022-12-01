package com.github.ai.autokpass.presentation.ui.screens.select_pattern

import com.github.ai.autokpass.domain.autotype.AutotypePatternFormatter
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.domain.fuzzy_search.FuzzyMatcher
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.presentation.ui.screens.select_pattern.model.SearchItem
import kotlinx.coroutines.withContext

class SelectPatternInteractor(
    private val dispatchers: Dispatchers,
    private val formatter: AutotypePatternFormatter,
    private val fuzzyMatcher: FuzzyMatcher
) {

    private val patterns = AutotypePattern.ALL
    private val titles = patterns.mapIndexed { idx, pattern -> formatPattern(idx, pattern) }

    fun loadAll(): Pair<List<AutotypePattern>, List<String>> {
        return Pair(patterns, titles)
    }

    suspend fun filter(
        query: String,
        patterns: List<AutotypePattern>,
        titles: List<String>
    ): List<SearchItem> =
        withContext(dispatchers.IO) {
            fuzzyMatcher.match(
                titles = titles,
                entries = patterns,
                query = query
            )
                .map { item ->
                    SearchItem(
                        pattern = item.entry,
                        text = item.title,
                        highlights = item.highlights
                    )
                }
        }

    private fun formatPattern(index: Int, pattern: AutotypePattern): String {
        return "${index + 1} ${formatter.format(pattern)}"
    }
}