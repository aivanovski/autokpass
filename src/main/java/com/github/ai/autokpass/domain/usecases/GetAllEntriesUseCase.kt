package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.extensions.getAllEntries
import com.github.ai.autokpass.extensions.toKeepassEntries
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.Result

class GetAllEntriesUseCase(
    private val readDbUseCase: ReadDatabaseUseCase
) {

    fun getAllEntries(password: String, filePath: String): Result<List<KeepassEntry>> {
        val dbResult = readDbUseCase.readDatabase(password, filePath)
        if (dbResult.isFailed()) {
            return dbResult.getErrorOrThrow()
        }

        val db = dbResult.getDataOrThrow()

        return Result.Success(db.getAllEntries().toKeepassEntries())
    }
}