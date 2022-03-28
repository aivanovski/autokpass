package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.formatter.EntryFormatter
import com.github.ai.autokpass.domain.selector.OptionSelector
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result

class SelectEntryUseCase(
    private val getEntriesUseCase: GetAllEntriesUseCase,
    private val entryFormatter: EntryFormatter,
    private val optionSelector: OptionSelector
) {

    fun selectEntry(password: String, args: ParsedArgs): Result<KeepassEntry?> {
        val getEntriesResult = getEntriesUseCase.getAllEntries(password, args.filePath)
        if (getEntriesResult.isFailed()) {
            return getEntriesResult.getErrorOrThrow()
        }

        val entries = getEntriesResult.getDataOrThrow()
        val options = entries.map { entryFormatter.format(it) }

        val selectedIdx = optionSelector.select(options)

        return Result.Success(selectedIdx?.let { entries[it] })
    }
}