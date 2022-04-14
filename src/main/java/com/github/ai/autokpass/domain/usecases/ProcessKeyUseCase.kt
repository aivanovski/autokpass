package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.process.ProcessExecutor

class ProcessKeyUseCase(
    private val processExecutor: ProcessExecutor,
    private val fileContentProvider: FileSystemProvider
) {

    fun processKeyWithCommand(command: String, keyPath: String): Result<String> {
        val keyBytes = fileContentProvider.openFile(keyPath).readAllBytes()
        return Result.Success(processExecutor.execute(keyBytes, command).trim())
    }
}