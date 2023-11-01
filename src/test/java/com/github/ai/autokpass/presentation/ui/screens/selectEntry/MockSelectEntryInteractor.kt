package com.github.ai.autokpass.presentation.ui.screens.selectEntry

import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.model.SearchItem

class MockSelectEntryInteractor(
    private val entries: List<KeepassEntry> = emptyList(),
    private val loadError: Result.Error? = null,
    private val filterError: Result.Error? = null
) : SelectEntryInteractor {

    override suspend fun loadAllEntries(
        key: KeepassKey,
        filePath: String
    ): Result<Pair<List<KeepassEntry>, List<String>>> {
        if (loadError != null) {
            return loadError
        }

        return Result.Success(
            Pair(
                entries,
                entries.map { entry -> entry.title }
            )
        )
    }

    override suspend fun filterEntries(
        allEntries: List<KeepassEntry>,
        allTitles: List<String>,
        query: String
    ): Result<List<SearchItem>> {
        if (filterError != null) {
            return filterError
        }

        val filteredEntries = entries
            .filter { entry -> entry.title.contains(query) }
            .map { entry ->
                SearchItem(
                    entry = entry,
                    text = entry.title,
                    highlights = emptyList()
                )
            }
        return Result.Success(filteredEntries)
    }
}