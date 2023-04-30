package com.github.ai.autokpass.presentation.ui.screens.settings

import com.github.ai.autokpass.data.config.ConfigRepository
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import kotlinx.coroutines.withContext

class SettingsInteractor(
    private val configRepository: ConfigRepository,
    private val dispatchers: Dispatchers
) {

    suspend fun loadConfig(): Result<ParsedConfig> =
        withContext(dispatchers.IO) {
            val config = configRepository.getCurrent()
            if (config.isFailed()) {
                Result.Success(ParsedConfig.EMPTY)
            } else {
                config
            }
        }

    suspend fun saveConfig(config: ParsedConfig): Result<Unit> =
        withContext(dispatchers.IO) {
            configRepository.save(config)
        }
}