package com.github.ai.autokpass.utils

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.model.Result
import io.mockk.every
import io.mockk.mockk

typealias PathAndBytes = Pair<String, ByteArray>
typealias PathAndException = Pair<String, Exception>

fun mockFSProvider(
    data: List<PathAndBytes> = emptyList(),
    errorOnRead: List<PathAndException> = emptyList(),
    errorsOnWrite: List<PathAndException> = emptyList(),
    notExists: List<String> = emptyList(),
    exists: List<String> = emptyList()
): FileSystemProvider {
    val provider = mockk<FileSystemProvider>()

    for ((path, bytes) in data) {
        every { provider.readFile(path) }.returns(Result.Success(bytes))
        every { provider.exists(path) }.returns(true)
    }

    for (path in notExists) {
        every { provider.exists(path) }.returns(false)
    }

    for (path in exists) {
        every { provider.exists(path) }.returns(true)
    }

    for ((path, exception) in errorOnRead) {
        every { provider.readFile(path) }.returns(Result.Error(exception))
    }

    for ((path, exception) in errorsOnWrite) {
        every { provider.writeFile(path, any()) }.returns(Result.Error(exception))
    }

    return provider
}

fun mockPropertyProvider(
    vararg variables: Pair<String, String>
): SystemPropertyProvider {
    val provider = mockk<SystemPropertyProvider>()

    for (environment in variables) {
        every { provider.getSystemProperty(environment.first) }.returns(environment.second)
    }

    return provider
}