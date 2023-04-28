package com.github.ai.autokpass.data.keepass.kotpass

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.data.keepass.KeepassDatabaseFactory
import com.github.ai.autokpass.domain.exception.InvalidPasswordException
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.database.Credentials
import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.database.decode
import io.github.anvell.kotpass.errors.CryptoError
import java.io.ByteArrayInputStream

class KotpassDatabaseFactory(
    private val fileSystemProvider: FileSystemProvider,
    private val processExecutor: ProcessExecutor,
    private val strings: StringResources
) : KeepassDatabaseFactory {

    override fun open(key: KeepassKey, filePath: String): Result<KeepassDatabase> {
        return try {
            val getCredentialsResult = getCredentials(key)
            if (getCredentialsResult.isFailed()) {
                return getCredentialsResult.asErrorOrThrow()
            }

            val credentials = getCredentialsResult.getDataOrThrow()
            val readFileResult = fileSystemProvider.readFile(filePath)
            if (readFileResult.isFailed()) {
                return readFileResult.asErrorOrThrow()
            }

            val stream = ByteArrayInputStream(readFileResult.getDataOrThrow())
            val db = KeePassDatabase.decode(stream, credentials)
            Result.Success(KotpassDatabase(db))
        } catch (e: Exception) {
            if (e is CryptoError.InvalidKey) {
                Result.Error(InvalidPasswordException(strings))
            } else {
                Result.Error(e)
            }
        }
    }

    private fun getCredentials(key: KeepassKey): Result<Credentials> {
        return when (key) {
            is KeepassKey.PasswordKey -> {
                Result.Success(
                    Credentials.from(EncryptedValue.fromString(key.password))
                )
            }

            is KeepassKey.FileKey -> {
                val readKeyResult = fileSystemProvider.readFile(key.file.path)
                if (readKeyResult.isFailed()) {
                    return readKeyResult.asErrorOrThrow()
                }

                val keyBytes = readKeyResult.getDataOrThrow()
                if (key.processingCommand == null) {
                    Result.Success(
                        Credentials.from(EncryptedValue.fromBinary(keyBytes))
                    )
                } else {
                    val processedKeyResult = processExecutor.execute(
                        keyBytes,
                        key.processingCommand
                    )
                    if (processedKeyResult.isFailed()) {
                        return processedKeyResult.asErrorOrThrow()
                    }

                    val processedKey = processedKeyResult.getDataOrThrow().trim()
                    Result.Success(
                        Credentials.from(EncryptedValue.fromBinary(processedKey.toByteArray()))
                    )
                }
            }
        }
    }
}