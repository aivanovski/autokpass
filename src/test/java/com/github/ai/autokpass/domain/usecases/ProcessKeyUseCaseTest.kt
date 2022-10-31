package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class ProcessKeyUseCaseTest {

    @Test
    fun `processKeyWithCommand should read key file and 'decrypt' it with gpg`() {
        // arrange
        val fsProvider = mockk<FileSystemProvider>()
        val executor = mockk<ProcessExecutor>()

        every { fsProvider.openFile(PATH) }.returns(ByteArrayInputStream(ENCRYPTED_KEY_CONTENT))
        every { executor.execute(ENCRYPTED_KEY_CONTENT, COMMAND) }.returns(DECRYPTED_KEY_CONTENT)

        // act
        val result = ProcessKeyUseCase(executor, fsProvider)
            .processKeyWithCommand(COMMAND, PATH)

        // assert
        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe(DECRYPTED_KEY_CONTENT)
    }

    companion object {
        private const val PATH = "/path"
        private const val COMMAND = "gpg --decrypt"
        private const val DECRYPTED_KEY_CONTENT = "decrypted-key-content"
        private val ENCRYPTED_KEY_CONTENT = "encrypted-key-content".toByteArray()
    }
}