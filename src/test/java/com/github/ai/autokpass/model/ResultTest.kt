package com.github.ai.autokpass.model

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.types.beTheSameInstanceAs
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.lang.ClassCastException

class ResultTest {

    @Test
    fun `isSucceeded should return true if instance is Success`() {
        Result.Success(VALUE).isSucceeded() shouldBe true
    }

    @Test
    fun `isSucceeded should return false if instance is Error`() {
        Result.Error(EXCEPTION).isSucceeded() shouldBe false
    }

    @Test
    fun `isFailed should return true if instance is Error`() {
        Result.Error(EXCEPTION).isFailed() shouldBe true
    }

    @Test
    fun `isFailed should return false if instance is Success`() {
        Result.Success(VALUE).isFailed() shouldBe false
    }

    @Test
    fun `getDataOrThrow should return value if instance is Success`() {
        Result.Success(VALUE).getDataOrThrow() shouldBe VALUE
    }

    @Test
    fun `getDataOrThrow should throw exception if instance is Error`() {
        assertThrows(ClassCastException::class.java) {
            Result.Error(EXCEPTION).getDataOrThrow()
        }
    }

    @Test
    fun `getDataOrNull should return data if instance is Success`() {
        Result.Success(VALUE).getDataOrNull() shouldBe VALUE
    }

    @Test
    fun `getDataOrNull should return null if instance is Error`() {
        val result: Result<Int> = Result.Error(EXCEPTION)
        result.getDataOrNull() should beNull()
    }

    @Test
    fun `asErrorOrThrow should return error if instance is Error`() {
        Result.Error(EXCEPTION).asErrorOrThrow() shouldBe Result.Error(EXCEPTION)
    }

    @Test
    fun `asErrorOrThrow should throw exception if instance is Success`() {
        assertThrows(ClassCastException::class.java) {
            Result.Success(VALUE).asErrorOrThrow()
        }
    }

    @Test
    fun `getExceptionOrThrow should return exception if instance is Error`() {
        Result.Error(EXCEPTION).getExceptionOrThrow() should beTheSameInstanceAs(EXCEPTION)
    }

    @Test
    fun `getExceptionOrThrow should throw if instance is Success`() {
        assertThrows(ClassCastException::class.java) {
            Result.Success(VALUE).getExceptionOrThrow()
        }
    }

    companion object {
        private const val VALUE = 123
        private val EXCEPTION = Exception()
    }
}