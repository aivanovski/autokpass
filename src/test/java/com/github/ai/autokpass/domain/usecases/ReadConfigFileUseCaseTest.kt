package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.data.file.DefaultFileSystemProvider
import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.SystemPropertyProvider
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ReadConfigFileUseCaseTest {

    @Test
    fun readConfigArgs() {
        val result = newUseCase().readConfigArgs()
    }

    private fun newUseCase(): ReadConfigFileUseCase {
        return ReadConfigFileUseCase(
            systemPropertyProvider = SystemPropertyProvider(),
            fileSystemProvider = DefaultFileSystemProvider()
        )
    }
}