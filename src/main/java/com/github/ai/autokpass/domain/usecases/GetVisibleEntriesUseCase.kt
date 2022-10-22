package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result

class GetVisibleEntriesUseCase(
    private val readDbUseCase: ReadDatabaseUseCase
) {

    fun getEntries(key: KeepassKey, filePath: String): Result<List<KeepassEntry>> {
        val dbResult = readDbUseCase.readDatabase(key, filePath)
        if (dbResult.isFailed()) {
            return dbResult.asErrorOrThrow()
        }

        val db = dbResult.getDataOrThrow()
        val visibleEntries = db.getAllEntries().filter { it.isAutotypeEnabled }

        return Result.Success(visibleEntries)
    }
}