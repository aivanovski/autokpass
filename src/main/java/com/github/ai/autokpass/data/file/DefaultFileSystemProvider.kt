package com.github.ai.autokpass.data.file

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class DefaultFileSystemProvider : FileSystemProvider {

    override fun exists(path: String): Boolean = File(path).exists()

    override fun isFile(path: String): Boolean = File(path).isFile

    override fun openFile(path: String): InputStream {
        return FileInputStream(File(path))
    }
}