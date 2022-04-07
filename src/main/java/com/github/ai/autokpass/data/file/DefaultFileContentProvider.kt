package com.github.ai.autokpass.data.file

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class DefaultFileContentProvider : FileContentProvider {

    override fun openFile(path: String): InputStream {
        return FileInputStream(File(path))
    }
}