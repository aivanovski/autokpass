package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.model.InputReaderType
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import java.io.File

class GetKeyUseCase(
    private val readPasswordUseCase: ReadPasswordUseCase,
    private val processKeyUseCase: ProcessKeyUseCase
) {

    fun getKey(
        inputReaderType: InputReaderType,
        dbFilePath: String,
        keyPath: String?,
        keyProcessingCommand: String?
    ): Result<KeepassKey> {
        when {
            keyPath == null -> {
                val passwordResult = readPasswordUseCase.readPassword(inputReaderType, dbFilePath)
                if (passwordResult.isFailed()) {
                    return passwordResult.asErrorOrThrow()
                }

                val key = KeepassKey.PasswordKey(passwordResult.getDataOrThrow())
                return Result.Success(key)
            }
            keyProcessingCommand != null -> {
                // TODO: add check that key is correct
                val processedKeyResult = processKeyUseCase.processKeyWithCommand(keyProcessingCommand, keyPath)
                if (processedKeyResult.isFailed()) {
                    return processedKeyResult.asErrorOrThrow()
                }

                val key = KeepassKey.PasswordKey(processedKeyResult.getDataOrThrow())
                return Result.Success(key)
            }
            else -> {
                // TODO: add check that key is correct
                val key = KeepassKey.FileKey(File(keyPath))
                return Result.Success(key)
            }
        }
    }
}