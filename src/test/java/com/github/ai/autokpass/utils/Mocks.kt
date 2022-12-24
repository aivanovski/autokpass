package com.github.ai.autokpass.utils

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.model.Result
import io.mockk.every
import io.mockk.mockk

typealias PathAndBytes = Pair<String, ByteArray>
typealias PathAndException = Pair<String, Exception>

fun mockFSProvider(
    data: List<PathAndBytes> = emptyList(),
    errors: List<PathAndException> = emptyList()
): FileSystemProvider {
    val provider = mockk<FileSystemProvider>()

    for ((path, bytes) in data) {
        every { provider.readFile(path) }.returns(Result.Success(bytes))
    }

    for ((path, exception) in errors) {
        every { provider.readFile(path) }.throws(exception)
    }

    return provider
}