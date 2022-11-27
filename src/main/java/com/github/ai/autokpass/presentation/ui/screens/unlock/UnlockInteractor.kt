package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.domain.usecases.ReadDatabaseUseCase
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import kotlinx.coroutines.withContext

class UnlockInteractor(
    private val dispatchers: Dispatchers,
    private val readDatabaseUseCase: ReadDatabaseUseCase
) {

    suspend fun unlockDatabase(
        password: String,
        filePath: String
    ): Result<Unit> {
        return withContext(dispatchers.IO) {
            readDatabaseUseCase.readDatabase(
                key = KeepassKey.PasswordKey(password),
                filePath = filePath
            )
                .mapWith(Unit)
        }
    }
}