package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.data.config.ConfigRepository
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.domain.usecases.ReadDatabaseUseCase
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import kotlinx.coroutines.withContext

class UnlockInteractorImpl(
    private val configRepository: ConfigRepository,
    private val dispatchers: Dispatchers,
    private val readDatabaseUseCase: ReadDatabaseUseCase
) : UnlockInteractor {

    override suspend fun loadConfig(): Result<ParsedConfig> =
        withContext(dispatchers.IO) {
            configRepository.getCurrent()
        }

    override suspend fun unlockDatabase(
        key: KeepassKey,
        filePath: String
    ): Result<Unit> {
        return withContext(dispatchers.IO) {
            readDatabaseUseCase.readDatabase(
                key = key,
                filePath = filePath
            )
                .mapWith(Unit)
        }
    }
}