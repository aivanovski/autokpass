package com.github.ai.autokpass.domain

import com.github.ai.autokpass.domain.autotype.AutotypeExecutor
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.printer.Printer
import com.github.ai.autokpass.domain.process.ProcessExecutor
import com.github.ai.autokpass.domain.selector.OptionSelector
import com.github.ai.autokpass.domain.usecases.GetAllEntriesUseCase
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.ParsedArgs

class Interactor(
    private val args: ParsedArgs,
    private val printer: Printer,
    private val processExecutor: ProcessExecutor,
    private val errorInteractor: ErrorInteractor,
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val autotypeExecutor: AutotypeExecutor,
    private val sequenceFactory: AutotypeSequenceFactory,
    private val selector: OptionSelector
) {

    fun run() {
        val getEntriesResult = getAllEntriesUseCase.getAllEntries(args.password, args.filePath)
        if (getEntriesResult.isFailed()) {
            errorInteractor.processAndExit(getEntriesResult.getErrorOrThrow())
        }

        val entries = getEntriesResult.getDataOrThrow()

        val selectedIdx = selector.show(formatEntries(entries))
            ?: errorInteractor.exit("Nothing was selected")

        val selectedEntry = entries[selectedIdx]
        val sequence = sequenceFactory.createAutotypeSequence(selectedEntry)
            ?: errorInteractor.exit("Nothing to autotype")

        autotypeExecutor.execute(sequence)
    }

    private fun formatEntries(entries: List<KeepassEntry>): List<String> {
        return entries.map { entry ->
            val sb = StringBuilder(entry.title.trim())

            if (entry.username.isNotBlank()) {
                sb.append(" ").append(entry.username.trim())

                if (entry.password.isNotBlank()) {
                    sb.append(":").append(entry.password.maskWith('*'))
                }
            }

            sb.toString()
        }
    }

    private fun String.maskWith(symbol: Char): String {
        val chars = this.map { symbol }
        return String(chars.toCharArray())
    }
}