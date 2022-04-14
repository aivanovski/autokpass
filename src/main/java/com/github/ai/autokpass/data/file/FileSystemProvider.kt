package com.github.ai.autokpass.data.file

import java.io.InputStream

interface FileSystemProvider {
    fun exists(path: String): Boolean
    fun isFile(path: String): Boolean
    fun openFile(path: String): InputStream
}