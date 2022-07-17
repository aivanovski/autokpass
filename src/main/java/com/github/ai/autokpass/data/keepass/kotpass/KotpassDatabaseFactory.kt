package com.github.ai.autokpass.data.keepass.kotpass

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.data.keepass.KeepassDatabaseFactory
import com.github.ai.autokpass.domain.exception.InvalidPasswordException
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.database.Credentials
import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.database.decode
import io.github.anvell.kotpass.errors.CryptoError

class KotpassDatabaseFactory(
    private val fileSystemProvider: FileSystemProvider
) : KeepassDatabaseFactory {

    override fun open(key: KeepassKey, filePath: String): Result<KeepassDatabase> {
        return try {
            val credentials = key.toCredentials()
            val content = fileSystemProvider.openFile(filePath)
            val db = KeePassDatabase.decode(content, credentials)
            Result.Success(KotpassDatabase(db))
        } catch (e: Exception) {
            if (e is CryptoError.InvalidKey) {
                Result.Error(InvalidPasswordException())
            } else {
                Result.Error(e)
            }
        }
    }

    private fun KeepassKey.toCredentials(): Credentials {
        return when (this) {
            is KeepassKey.PasswordKey -> Credentials.from(EncryptedValue.fromString(password))
            is KeepassKey.FileKey -> Credentials.from(
                EncryptedValue.fromBinary(fileSystemProvider.openFile(file.path).readAllBytes())
            )
            is KeepassKey.XmlFileKey -> throw UnsupportedOperationException("XML key file is not supported")
        }
    }
}