package com.github.ai.autokpass.presentation.ui.screens.settings

import com.github.ai.autokpass.data.config.ConfigRepository
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import kotlinx.coroutines.withContext

class SettingsInteractor(
    private val getOSTypeUseCase: GetOSTypeUseCase,
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

    suspend fun loadAvailableAutotypeTypes(): List<AutotypeExecutorType> =
        withContext(dispatchers.IO) {
            val osTypeResult = getOSTypeUseCase.getOSType()
            if (osTypeResult.isFailed()) {
                return@withContext AutotypeExecutorType.values().toList()
            }

            getAutotypeTypesByOsType(osTypeResult.getDataOrThrow())
        }

    suspend fun saveConfig(config: ParsedConfig): Result<Unit> =
        withContext(dispatchers.IO) {
            configRepository.save(config)
        }

    private fun getAutotypeTypesByOsType(osType: OSType): List<AutotypeExecutorType> {
        return when (osType) {
            OSType.LINUX -> listOf(AutotypeExecutorType.XDOTOOL)
            OSType.MAC_OS -> {
                listOf(
                    AutotypeExecutorType.OSA_SCRIPT,
                    AutotypeExecutorType.CLICLICK
                )
            }
        }
    }
}