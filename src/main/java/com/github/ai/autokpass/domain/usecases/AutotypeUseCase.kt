package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.autotype.AutotypeExecutor
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result

class AutotypeUseCase(
    private val autotypeExecutor: AutotypeExecutor,
    private val sequenceFactory: AutotypeSequenceFactory,
    private val printer: Printer
) {

    fun doAutotype(
        entry: KeepassEntry,
        pattern: AutotypePattern,
        args: ParsedArgs
    ): Result<Unit> {
        val sequence = sequenceFactory.createAutotypeSequence(entry, pattern)
            ?: return Result.Error(AutokpassException("Nothing to autotype"))

        args.delayInSeconds?.let { delay ->
            printer.println("Autotype will start after $delay seconds delay.")
            Thread.sleep(delay * 1000)
        }

        autotypeExecutor.execute(sequence)

        return Result.Success(Unit)
    }
}