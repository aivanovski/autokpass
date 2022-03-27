package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.autotype.AutotypeExecutor
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result

class AutotypeUseCase(
    private val autotypeExecutor: AutotypeExecutor,
    private val getEntriesUseCase: GetAllEntriesUseCase,
    private val sequenceFactory: AutotypeSequenceFactory
) {

    fun autotypeValues(args: ParsedArgs): Result<Unit> {
        val getEntriesResult = getEntriesUseCase.getAllEntries(args.password, args.filePath)
        if (getEntriesResult.isFailed()) {
            return getEntriesResult.getErrorOrThrow()
        }

        val entries = getEntriesResult.getDataOrThrow()
        val entry = entries.firstOrNull { it.uid == args.uid }
            ?: return Result.Error(AutokpassException("Failed to find entry by uid: ${args.uid}"))

        val pattern = args.pattern ?: AutotypePattern.DEFAULT_PATTERN

        val sequence = sequenceFactory.createAutotypeSequence(entry, pattern)
            ?: return Result.Error(AutokpassException("Nothing to autotype"))

        args.delayInSeconds?.let { delay ->
            Thread.sleep(delay * 1000)
        }

        autotypeExecutor.execute(sequence)

        return Result.Success(Unit)
    }
}