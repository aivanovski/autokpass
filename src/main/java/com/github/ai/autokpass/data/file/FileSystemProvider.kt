package com.github.ai.autokpass.data.file

import java.io.InputStream

interface FileSystemProvider {
    fun exists(path: String): Boolean
    fun isFile(path: String): Boolean
    // TODO: should return result
    fun openFile(path: String): InputStream
}