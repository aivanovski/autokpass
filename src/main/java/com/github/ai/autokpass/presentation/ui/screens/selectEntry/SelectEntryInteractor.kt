package com.github.ai.autokpass.presentation.ui.screens.selectEntry

import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.model.SearchItem

interface SelectEntryInteractor {
    suspend fun loadAllEntries(
        key: KeepassKey,
        filePath: String
    ): Result<Pair<List<KeepassEntry>, List<String>>>

    suspend fun filterEntries(
        allEntries: List<KeepassEntry>,
        allTitles: List<String>,
        query: String
    ): Result<List<SearchItem>>
}