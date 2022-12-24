package com.github.ai.autokpass.data.file

import com.github.ai.autokpass.model.Result

interface FileSystemProvider {
    fun exists(path: String): Boolean
    fun isFile(path: String): Boolean
    fun readFile(path: String): Result<ByteArray>
}