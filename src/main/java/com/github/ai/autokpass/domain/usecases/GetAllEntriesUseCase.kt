package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.Config
import com.github.ai.autokpass.domain.exception.IncorrectPasswordException
import com.github.ai.autokpass.extensions.getAllEntries
import com.github.ai.autokpass.extensions.toKeepassEntries
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.model.Result
import org.linguafranca.pwdb.kdbx.KdbxCreds
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase
import java.io.File
import java.io.FileInputStream

class GetAllEntriesUseCase {

    fun getAllEntries(password: String, filePath: String): Result<List<KeepassEntry>> {
        val dbResult = readDatabase(password, filePath)
        if (dbResult.isFailed()) {
            return dbResult.getErrorOrThrow()
        }

        val db = dbResult.getDataOrThrow()

        return Result.Success(db.getAllEntries().toKeepassEntries())
    }

    private fun readDatabase(password: String, filePath: String): Result<SimpleDatabase> {
        return try {
            val input = FileInputStream(File(filePath))
            Result.Success(SimpleDatabase.load(KdbxCreds(password.toByteArray()), input))
        } catch (e: Exception) {
            if (isIncorrectPasswordException(e)) {
                Result.Error(IncorrectPasswordException())
            } else {
                if (Config.DEBUG) {
                    e.printStackTrace()
                }
                Result.Error(e)
            }
        }
    }

    private fun isIncorrectPasswordException(exception: Exception): Boolean {
        return exception is java.lang.IllegalStateException &&
                exception.message?.contains("Inconsistent stream start bytes") == true
    }
}