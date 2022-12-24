package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.Errors.FAILED_TO_COMPILE_AUTOTYPE_SEQUENCE
import com.github.ai.autokpass.domain.autotype.AutotypeExecutorFactory
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.autotype.ThreadThrottler
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.Result

// TODO: remove
class AutotypeUseCase(
    private val autotypeExecutorFactory: AutotypeExecutorFactory,
    private val sequenceFactory: AutotypeSequenceFactory,
    private val throttler: ThreadThrottler,
    private val printer: Printer
) {

    fun doAutotype(
        executorType: AutotypeExecutorType,
        entry: KeepassEntry,
        pattern: AutotypePattern,
        delayBetweenActions: Long,
        startDelayInSeconds: Long?
    ): Result<Unit> {
        val sequence = sequenceFactory.createAutotypeSequence(entry, pattern, delayBetweenActions)
            ?: return Result.Error(AutokpassException(FAILED_TO_COMPILE_AUTOTYPE_SEQUENCE))

        startDelayInSeconds?.let {
            printer.println(String.format(DELAY_MESSAGE, it))
            throttler.sleep(it * 1000)
        }

        autotypeExecutorFactory.getExecutor(executorType).execute(sequence)

        return Result.Success(Unit)
    }

    companion object {
        const val DELAY_MESSAGE = "Autotype will start after %s seconds delay."
    }
}