package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.formatter.EntryFormatter
import com.github.ai.autokpass.domain.printer.Printer
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result

class PrintAllEntriesUseCase(
    private val getEntriesUseCase: GetAllEntriesUseCase,
    private val entryFormatter: EntryFormatter,
    private val printer: Printer
) {

    fun printAllEntries(args: ParsedArgs): Result<Unit> {
        val getEntriesResult = getEntriesUseCase.getAllEntries(args.password, args.filePath)
        if (getEntriesResult.isFailed()) {
            return getEntriesResult.getErrorOrThrow()
        }

        val entries = getEntriesResult.getDataOrThrow()

        entries
            .filter { it.isNotEmpty() }
            .forEach { printer.println(entryFormatter.format(it)) }

        return Result.Success(Unit)
    }
}