package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.data.file.FileContentProvider
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.domain.exception.InvalidPasswordException
import com.github.ai.autokpass.model.KeepassKey.FileKey
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.model.KeepassKey.XmlFileKey
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.utils.resourceAsStream
import com.google.common.truth.Truth.assertThat
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.io.File

class ReadDatabaseUseCaseTest {

    @Test
    fun `readDatabase should return db with password`() {
        // arrange
        val fileProvider = mockk<FileContentProvider>()
        every { fileProvider.openFile(DB_PATH) }.returns(resourceAsStream("db-with-password.kdbx"))

        // act
        val result = ReadDatabaseUseCase(fileProvider)
            .readDatabase(PasswordKey(DEFAULT_PASSWORD), DB_PATH)

        // assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.getDataOrThrow()).isNotNull()

        verify { fileProvider.openFile(DB_PATH) }
        confirmVerified(fileProvider)
    }

    @Test
    fun `readDatabase should return InvalidPasswordException in case incorrect password`() {
        // arrange
        val fileProvider = mockk<FileContentProvider>()
        every { fileProvider.openFile(DB_PATH) }.returns(resourceAsStream("db-with-password.kdbx"))

        // act
        val result = ReadDatabaseUseCase(fileProvider)
            .readDatabase(PasswordKey(""), DB_PATH)

        // assert
        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat(result.getErrorOrThrow().exception).isInstanceOf(InvalidPasswordException::class.java)
    }

    @Test
    fun `readDatabase should return db with key file`() {
        // arrange
        val fileProvider = mockk<FileContentProvider>()
        every { fileProvider.openFile(DB_PATH) }.returns(resourceAsStream("db-with-bin-key.kdbx"))
        every { fileProvider.openFile(KEY_PATH) }.returns(resourceAsStream("bin-key"))

        // act
        val result = ReadDatabaseUseCase(fileProvider)
            .readDatabase(FileKey(File(KEY_PATH)), DB_PATH)

        // assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.getDataOrThrow()).isNotNull()

        verify { fileProvider.openFile(DB_PATH) }
        verify { fileProvider.openFile(KEY_PATH) }
        confirmVerified(fileProvider)
    }

    @Test
    fun `readDatabase should return db with xml key`() {
        // arrange
        val fileProvider = mockk<FileContentProvider>()
        every { fileProvider.openFile(DB_PATH) }.returns(resourceAsStream("db-with-xml-key.kdbx"))
        every { fileProvider.openFile(KEY_PATH) }.returns(resourceAsStream("xml-key"))

        // act
        val result = ReadDatabaseUseCase(fileProvider)
            .readDatabase(XmlFileKey(File(KEY_PATH)), DB_PATH)

        // assert
        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.getDataOrThrow()).isNotNull()

        verify { fileProvider.openFile(DB_PATH) }
        verify { fileProvider.openFile(KEY_PATH) }
        confirmVerified(fileProvider)
    }

    companion object {
        private const val DEFAULT_PASSWORD = "abc123"
        private const val DB_PATH = "/path/db.kdbx"
        private const val KEY_PATH = "/path/key"
    }
}