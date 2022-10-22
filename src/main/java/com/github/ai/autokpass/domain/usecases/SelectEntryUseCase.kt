package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.formatter.EntryFormatter
import com.github.ai.autokpass.presentation.selector.OptionSelector
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result

class SelectEntryUseCase(
    private val getEntriesUseCase: GetVisibleEntriesUseCase,
    private val entryFormatter: EntryFormatter,
    private val optionSelector: OptionSelector
) {

    fun selectEntry(key: KeepassKey, dbFilePath: String): Result<KeepassEntry?> {
        val getEntriesResult = getEntriesUseCase.getEntries(key, dbFilePath)
        if (getEntriesResult.isFailed()) {
            return getEntriesResult.asErrorOrThrow()
        }

        val entries = getEntriesResult.getDataOrThrow()
        val options = entries.map { entryFormatter.format(it) }

        val selectionResult = optionSelector.select(options)
        if (selectionResult.isFailed()) {
            return selectionResult.asErrorOrThrow()
        }

        val selectionIdx = selectionResult.getDataOrThrow()
        return Result.Success(entries[selectionIdx])
    }
}