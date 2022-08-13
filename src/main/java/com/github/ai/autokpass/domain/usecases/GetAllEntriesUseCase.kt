package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result

class GetAllEntriesUseCase(
    private val readDbUseCase: ReadDatabaseUseCase
) {

    fun getAllEntries(key: KeepassKey, filePath: String): Result<List<KeepassEntry>> {
        val dbResult = readDbUseCase.readDatabase(key, filePath)
        if (dbResult.isFailed()) {
            return dbResult.asErrorOrThrow()
        }

        val db = dbResult.getDataOrThrow()

        return Result.Success(db.getAllEntries())
    }
}