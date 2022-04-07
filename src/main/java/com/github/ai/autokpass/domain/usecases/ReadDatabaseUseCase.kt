package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.data.file.FileContentProvider
import com.github.ai.autokpass.domain.exception.InvalidPasswordException
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import org.linguafranca.pwdb.Credentials
import org.linguafranca.pwdb.kdbx.KdbxCreds
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase

class ReadDatabaseUseCase(
    private val fileContentProvider: FileContentProvider
) {

    fun readDatabase(key: KeepassKey, filePath: String): Result<SimpleDatabase> {
        return try {
            val input = fileContentProvider.openFile(filePath)
            Result.Success(SimpleDatabase.load(key.toCredentials(), input))
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
            is KeepassKey.FileKey -> KdbxCreds(fileContentProvider.openFile(file.path).readAllBytes())
            is KeepassKey.XmlFileKey -> KdbxCreds(byteArrayOf(), fileContentProvider.openFile(file.path))
        }
    }
}