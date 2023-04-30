package com.github.ai.autokpass.utils

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.model.Result

class WritableFileSystemProvider : FileSystemProvider {

    private val data = mutableMapOf<String, String>()

    override fun exists(path: String): Boolean {
        throw NotImplementedError()
    }

    override fun isFile(path: String): Boolean {
        throw NotImplementedError()
    }

    override fun readFile(path: String): Result<ByteArray> {
        throw NotImplementedError()
    }

    override fun writeFile(path: String, bytes: ByteArray): Result<Unit> {
        data[path] = String(bytes)
        return Result.Success(Unit)
    }

    fun getWrittenFile(path: String): String? = data[path]
}