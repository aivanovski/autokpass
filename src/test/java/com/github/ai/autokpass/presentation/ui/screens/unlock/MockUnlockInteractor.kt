package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.TestData.COMMAND
import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.EXCEPTION
import com.github.ai.autokpass.TestData.KEY_PATH
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import java.io.File

class MockUnlockInteractor(
    private val config: Result<ParsedConfig>,
    private val passwordKey: PasswordKey? = newPasswordKey(),
    private val fileKey: KeepassKey.FileKey? = newFileKey()
) : UnlockInteractor {

    override suspend fun loadConfig(): Result<ParsedConfig> {
        return config
    }

    override suspend fun unlockDatabase(key: KeepassKey, filePath: String): Result<Unit> {
        return when (key) {
            is PasswordKey -> {
                if (key == passwordKey) {
                    Result.Success(Unit)
                } else {
                    Result.Error(EXCEPTION)
                }
            }
            is KeepassKey.FileKey -> {
                if (key == fileKey) {
                    Result.Success(Unit)
                } else {
                    Result.Error(EXCEPTION)
                }
            }
        }
    }

    companion object {
        private fun newPasswordKey(): PasswordKey =
            PasswordKey(DB_PASSWORD)

        private fun newFileKey(): KeepassKey.FileKey =
            KeepassKey.FileKey(
                file = File(KEY_PATH),
                processingCommand = COMMAND
            )
    }
}