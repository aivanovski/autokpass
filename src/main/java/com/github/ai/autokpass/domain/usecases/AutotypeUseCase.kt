package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.autotype.AutotypeExecutorProvider
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.autotype.ThreadThrottler
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.Result

class AutotypeUseCase(
    private val executorProvider: AutotypeExecutorProvider,
    private val sequenceFactory: AutotypeSequenceFactory,
    private val throttler: ThreadThrottler,
    private val printer: Printer
) {

    fun doAutotype(
        executorType: AutotypeExecutorType,
        entry: KeepassEntry,
        pattern: AutotypePattern,
        delayInSeconds: Long?
    ): Result<Unit> {
        val sequence = sequenceFactory.createAutotypeSequence(entry, pattern)
            ?: return Result.Error(AutokpassException("Nothing to autotype"))

        delayInSeconds?.let {
            printer.println("Autotype will start after $it seconds delay.")
            throttler.sleep(it * 1000)
        }

        executorProvider.getExecutor(executorType).execute(sequence)

        return Result.Success(Unit)
    }
}