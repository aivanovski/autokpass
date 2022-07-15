package com.github.ai.autokpass.utils

import com.github.ai.autokpass.data.file.FileSystemProvider
import io.mockk.every
import io.mockk.mockk
import java.io.InputStream

typealias PathAndInputStream = Pair<String, InputStream>
typealias PathAndException = Pair<String, Exception>

fun mockFSProvider(
    data: List<PathAndInputStream> = emptyList(),
    errors: List<PathAndException> = emptyList()
): FileSystemProvider {
    val provider = mockk<FileSystemProvider>()

    for ((path, stream) in data) {
        every { provider.openFile(path) }.returns(stream)
    }

    for ((path, exception) in errors) {
        every { provider.openFile(path) }.throws(exception)
    }

    return provider
}