package com.github.ai.autokpass.presentation.ui.screens.selectEntry

import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.domain.formatter.EntryFormatter
import com.github.ai.autokpass.domain.fuzzySearch.FuzzyMatcher
import com.github.ai.autokpass.domain.usecases.GetVisibleEntriesUseCase
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.model.SearchItem
import kotlinx.coroutines.withContext

class SelectEntryInteractor(
    private val getEntriesUseCase: GetVisibleEntriesUseCase,
    private val dispatchers: Dispatchers,
    private val fuzzyMatcher: FuzzyMatcher,
    private val formatter: EntryFormatter
) {

    suspend fun loadAll(
        key: KeepassKey,
        filePath: String
    ): Result<Pair<List<KeepassEntry>, List<String>>> =
        withContext(dispatchers.IO) {
            val getEntriesResult = getEntriesUseCase.getEntries(key, filePath)
            if (getEntriesResult.isFailed()) {
                return@withContext getEntriesResult.asErrorOrThrow()
            }

            val entries = getEntriesResult.getDataOrThrow()
                .filter { entry -> entry.isAutotypeEnabled }

            val titles = entries.map { entry -> formatter.format(entry) }

            Result.Success(Pair(entries, titles))
        }

    suspend fun filter(
        allEntries: List<KeepassEntry>,
        allTitles: List<String>,
        query: String
    ): Result<List<SearchItem>> =
        withContext(dispatchers.IO) {
            if (query.isEmpty()) {
                return@withContext Result.Success(
                    allEntries.map { entry ->
                        SearchItem(
                            entry = entry,
                            text = formatter.format(entry),
                            highlights = emptyList()
                        )
                    }
                )
            }

            val result = fuzzyMatcher.match(
                titles = allTitles,
                entries = allEntries,
                query = query
            )
                .map { item ->
                    SearchItem(
                        entry = item.entry,
                        text = item.title,
                        highlights = item.highlights
                    )
                }

            Result.Success(result)
        }
}