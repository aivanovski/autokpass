package com.github.ai.autokpass.data.keepass.keepassjava2

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.data.keepass.KeepassDatabaseFactory
import com.github.ai.autokpass.domain.exception.InvalidPasswordException
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import org.linguafranca.pwdb.Credentials
import org.linguafranca.pwdb.kdbx.KdbxCreds
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase

class KeepassJava2DatabaseFactory(
    private val fileSystemProvider: FileSystemProvider
) : KeepassDatabaseFactory {

    override fun open(key: KeepassKey, filePath: String): Result<KeepassDatabase> {
        return try {
            val credentials = key.toCredentials()
            val input = fileSystemProvider.openFile(filePath)
            val db = SimpleDatabase.load(credentials, input)
            Result.Success(KeepassJava2Database(db))
        } catch (e: Exception) {
            if (isIncorrectPasswordException(e)) {
                Result.Error(InvalidPasswordException())
            } else {
                Result.Error(e)
            }
        }
    }

    private fun isIncorrectPasswordException(exception: Exception): Boolean {
        return exception is java.lang.IllegalStateException &&
            exception.message?.contains("Inconsistent stream start bytes") == true
    }

    private fun KeepassKey.toCredentials(): Credentials {
        return when (this) {
            is KeepassKey.PasswordKey -> KdbxCreds(password.toByteArray())
            is KeepassKey.FileKey -> KdbxCreds(fileSystemProvider.openFile(file.path).readAllBytes())
            is KeepassKey.XmlFileKey -> KdbxCreds(byteArrayOf(), fileSystemProvider.openFile(file.path))
        }
    }
}