package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result

interface UnlockInteractor {
    suspend fun loadConfig(): Result<ParsedConfig>

    suspend fun unlockDatabase(
        key: KeepassKey,
        filePath: String
    ): Result<Unit>
}