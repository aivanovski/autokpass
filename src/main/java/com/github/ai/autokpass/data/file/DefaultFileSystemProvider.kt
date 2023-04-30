package com.github.ai.autokpass.data.file

import com.github.ai.autokpass.model.Result
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class DefaultFileSystemProvider : FileSystemProvider {

    override fun exists(path: String): Boolean = File(path).exists()

    override fun isFile(path: String): Boolean = File(path).isFile

    override fun readFile(path: String): Result<ByteArray> {
        return if (exists(path)) {
            try {
                val bytes = FileInputStream(File(path)).readAllBytes()
                Result.Success(bytes)
            } catch (exception: IOException) {
                Result.Error(exception)
            }
        } else {
            Result.Error(FileNotFoundException(path))
        }
    }

    override fun writeFile(path: String, bytes: ByteArray): Result<Unit> {
        return try {
            BufferedOutputStream(FileOutputStream(path, false))
                .use { out ->
                    out.write(bytes)
                }

            Result.Success(Unit)
        } catch (exception: IOException) {
            Result.Error(exception)
        }
    }
}