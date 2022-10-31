package com.github.ai.autokpass.data.keepass.kotpass

import com.github.ai.autokpass.TestData
import com.github.ai.autokpass.TestData.DB_WITH_PASSWORD
import com.github.ai.autokpass.TestData.INVALID_DB_PASSWORD
import com.github.ai.autokpass.asFileKey
import com.github.ai.autokpass.asPasswordKey
import com.github.ai.autokpass.domain.exception.InvalidPasswordException
import com.github.ai.autokpass.getFilePath
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.toKeepassKey
import com.github.ai.autokpass.utils.mockFSProvider
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.nulls.beNull
import io.mockk.confirmVerified
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.io.IOException

class KotpassDatabaseFactoryTest {

    @Test
    fun `open should load database with password`() {
        // arrange
        val db = DB_WITH_PASSWORD
        val key = db.key.asPasswordKey()
        val fsProvider = mockFSProvider(
            data = listOf(
                db.getFilePath() to db.asStream()
            )
        )

        // act
        val result = KotpassDatabaseFactory(fsProvider)
            .open(
                key = key.toKeepassKey(),
                filePath = db.getFilePath()
            )

        // assert
        verify { fsProvider.openFile(db.getFilePath()) }
        confirmVerified()

        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldNot beNull()
    }

    @Test
    fun `open should load database with binary key`() {
        // arrange
        val db = TestData.DB_WITH_BINARY_KEY
        val key = db.key.asFileKey()
        val fsProvider = mockFSProvider(
            data = listOf(
                db.getFilePath() to db.asStream(),
                key.getFilePath() to key.asStream()
            )
        )

        // act
        val result = KotpassDatabaseFactory(fsProvider)
            .open(
                key = key.toKeepassKey(),
                filePath = db.getFilePath()
            )

        // assert
        verify { fsProvider.openFile(key.getFilePath()) }
        verify { fsProvider.openFile(db.getFilePath()) }
        confirmVerified()

        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldNot beNull()
    }

    @Test
    fun `open should return InvalidPasswordException if password is incorrect`() {
        // arrange
        val db = DB_WITH_PASSWORD
        val fsProvider = mockFSProvider(
            data = listOf(
                db.getFilePath() to db.asStream()
            )
        )

        // act
        val result = KotpassDatabaseFactory(fsProvider)
            .open(
                key = KeepassKey.PasswordKey(INVALID_DB_PASSWORD),
                filePath = db.getFilePath()
            )

        // assert
        verify { fsProvider.openFile(db.getFilePath()) }
        confirmVerified()

        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<InvalidPasswordException>()
    }

    @Test
    fun `open should return IOException if database file not found`() {
        // arrange
        val db = DB_WITH_PASSWORD
        val fsProvider = mockFSProvider(
            errors = listOf(
                db.getFilePath() to IOException()
            )
        )

        // act
        val result = KotpassDatabaseFactory(fsProvider)
            .open(
                key = KeepassKey.PasswordKey(INVALID_DB_PASSWORD),
                filePath = db.getFilePath()
            )

        // assert
        verify { fsProvider.openFile(db.getFilePath()) }
        confirmVerified()

        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<IOException>()
    }

    @Test
    fun `open should return IOException if key file not found`() {
        // arrange
        val db = TestData.DB_WITH_BINARY_KEY
        val key = db.key.asFileKey()
        val fsProvider = mockFSProvider(
            errors = listOf(
                key.getFilePath() to IOException()
            )
        )

        // act
        val result = KotpassDatabaseFactory(fsProvider)
            .open(
                key = key.toKeepassKey(),
                filePath = db.getFilePath()
            )

        // assert
        verify { fsProvider.openFile(key.getFilePath()) }
        confirmVerified()

        result.isFailed() shouldBe true
        result.getExceptionOrThrow() should beInstanceOf<IOException>()
    }
}