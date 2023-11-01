package com.github.ai.autokpass.presentation.ui.screens.selectPattern

import com.github.ai.autokpass.domain.autotype.AutotypePatternFactory
import com.github.ai.autokpass.domain.autotype.AutotypePatternFormatter
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.domain.fuzzySearch.FuzzyMatcher
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.model.SearchItem
import kotlinx.coroutines.withContext

class SelectPatternInteractorImpl(
    private val dispatchers: Dispatchers,
    private val factory: AutotypePatternFactory,
    private val formatter: AutotypePatternFormatter,
    private val fuzzyMatcher: FuzzyMatcher
) : SelectPatternInteractor {

    override suspend fun loadData(entry: KeepassEntry): Pair<List<AutotypePattern>, List<String>> =
        withContext(dispatchers.IO) {
            val patterns = factory.createPatternsForEntry(entry)
            val titles = patterns.mapIndexed { idx, pattern -> formatPattern(idx, pattern) }
            Pair(patterns, titles)
        }

    override suspend fun filter(
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