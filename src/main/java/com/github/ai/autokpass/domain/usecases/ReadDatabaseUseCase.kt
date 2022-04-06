package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.exception.InvalidPasswordException
import com.github.ai.autokpass.model.Result
import org.linguafranca.pwdb.kdbx.KdbxCreds
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase
import java.io.File
import java.io.FileInputStream

class ReadDatabaseUseCase {

    fun readDatabase(password: String, filePath: String): Result<SimpleDatabase> {
        return try {
            val input = FileInputStream(File(filePath))
            Result.Success(SimpleDatabase.load(KdbxCreds(password.toByteArray()), input))
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
}