package com.github.ai.autokpass.data.file

import java.io.InputStream

interface FileContentProvider {
    fun openFile(path: String): InputStream
}