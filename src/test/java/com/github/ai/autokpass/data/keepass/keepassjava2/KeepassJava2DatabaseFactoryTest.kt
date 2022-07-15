package com.github.ai.autokpass.data.keepass.keepassjava2

import com.github.ai.autokpass.TestData
import com.github.ai.autokpass.TestData.DB_WITH_BINARY_KEY
import com.github.ai.autokpass.TestData.DB_WITH_PASSWORD
import com.github.ai.autokpass.TestData.DB_WITH_XML_KEY
import com.github.ai.autokpass.domain.exception.InvalidPasswordException
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.utils.mockFSProvider
import com.google.common.truth.Truth.assertThat
import io.mockk.confirmVerified
import io.mockk.verify
import org.junit.Test
import java.io.File
import java.io.IOException

class KeepassJava2DatabaseFactoryTest {

    @Test
    fun `open should load database with password`() {
        // arrange
        val testData = DB_WITH_PASSWORD
        val password = (testData.key as TestData.TestKey.PasswordKey).password
        val path = "/" + testData.filename
        val fsProvider = mockFSProvider(
            data = listOf(
                path to testData.asStream()
            )
        )

        // act
        val result = KeepassJava2DatabaseFactory(fsProvider)
            .open(
                key = KeepassKey.PasswordKey(password),
                filePath = path
            )

        // assert
        verify { fsProvider.openFile(path) }
        confirmVerified()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.getDataOrThrow()).isNotNull()
    }

    @Test
    fun `open should load database with binary key`() {
        // arrange
        val testData = DB_WITH_BINARY_KEY
        val binKey = (testData.key as TestData.TestKey.FileKey)
        val path = "/" + testData.filename
        val keyPath = "/" + binKey.filename
        val fsProvider = mockFSProvider(
            data = listOf(
                path to testData.asStream(),
                keyPath to binKey.asStream()
            )
        )

        // act
        val result = KeepassJava2DatabaseFactory(fsProvider)
            .open(
                key = KeepassKey.FileKey(File(keyPath)),
                filePath = path
            )

        // assert
        verify { fsProvider.openFile(keyPath) }
        verify { fsProvider.openFile(path) }
        confirmVerified()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.getDataOrThrow()).isNotNull()
    }

    @Test
    fun `open should load database with xml key`() {
        // arrange
        val testData = DB_WITH_XML_KEY
        val xmlKey = (testData.key as TestData.TestKey.XmlFileKey)
        val path = "/" + testData.filename
        val keyPath = "/" + xmlKey.filename
        val fsProvider = mockFSProvider(
            data = listOf(
                path to testData.asStream(),
                keyPath to xmlKey.asStream()
            )
        )

        // act
        val result = KeepassJava2DatabaseFactory(fsProvider)
            .open(
                key = KeepassKey.XmlFileKey(File(keyPath)),
                filePath = path
            )

        // assert
        verify { fsProvider.openFile(keyPath) }
        verify { fsProvider.openFile(path) }
        confirmVerified()

        assertThat(result).isInstanceOf(Result.Success::class.java)
        assertThat(result.getDataOrThrow()).isNotNull()
    }

    @Test
    fun `open should return InvalidPasswordException if password is incorrect`() {
        // arrange
        val testData = DB_WITH_PASSWORD
        val path = "/" + testData.filename
        val fsProvider = mockFSProvider(
            data = listOf(
                path to testData.asStream()
            )
        )

        // act
        val result = KeepassJava2DatabaseFactory(fsProvider)
            .open(
                key = KeepassKey.PasswordKey(INVALID_PASSWORD),
                filePath = path
            )

        // assert
        verify { fsProvider.openFile(path) }
        confirmVerified()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat(result.getErrorOrThrow().exception).isInstanceOf(InvalidPasswordException::class.java)
    }

    @Test
    fun `open should return IOException if database file not found`() {
        // arrange
        val testData = DB_WITH_PASSWORD
        val path = "/" + testData.filename
        val fsProvider = mockFSProvider(
            errors = listOf(
                path to IOException()
            )
        )

        // act
        val result = KeepassJava2DatabaseFactory(fsProvider)
            .open(
                key = KeepassKey.PasswordKey(INVALID_PASSWORD),
                filePath = path
            )

        // assert
        verify { fsProvider.openFile(path) }
        confirmVerified()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat(result.getErrorOrThrow().exception).isInstanceOf(IOException::class.java)
    }

    @Test
    fun `open should return IOException if key file not found`() {
        // arrange
        val testData = DB_WITH_BINARY_KEY
        val binKey = (testData.key as TestData.TestKey.FileKey)
        val path = "/" + testData.filename
        val keyPath = "/" + binKey.filename
        val fsProvider = mockFSProvider(
            errors = listOf(
                keyPath to IOException()
            )
        )

        // act
        val result = KeepassJava2DatabaseFactory(fsProvider)
            .open(
                key = KeepassKey.FileKey(File(keyPath)),
                filePath = path
            )

        // assert
        verify { fsProvider.openFile(keyPath) }
        confirmVerified()

        assertThat(result).isInstanceOf(Result.Error::class.java)
        assertThat(result.getErrorOrThrow().exception).isInstanceOf(IOException::class.java)
    }

    companion object {
        private const val INVALID_PASSWORD = "123456"
    }
}