package com.github.ai.autokpass.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.lang.ClassCastException

class ResultTest {

    @Test
    fun `isSucceeded should return true if instance is Success`() {
        assertThat(Result.Success(VALUE).isSucceeded())
            .isTrue()
    }

    @Test
    fun `isSucceeded should return false if instance is Error`() {
        assertThat(Result.Error(EXCEPTION).isSucceeded())
            .isFalse()
    }

    @Test
    fun `isFailed should return true if instance is Error`() {
        assertThat(Result.Error(EXCEPTION).isFailed())
            .isTrue()
    }

    @Test
    fun `isFailed should return false if instance is Success`() {
        assertThat(Result.Success(VALUE).isFailed())
            .isFalse()
    }

    @Test
    fun `getDataOrThrow should return value if instance is Success`() {
        assertThat(Result.Success(VALUE).getDataOrThrow())
            .isEqualTo(VALUE)
    }

    @Test
    fun `getDataOrThrow should throw exception if instance is Error`() {
        assertThrows(ClassCastException::class.java) {
            Result.Error(EXCEPTION).getDataOrThrow()
        }
    }

    @Test
    fun `getDataOrNull should return data if instance is Success`() {
        assertThat(Result.Success(VALUE).getDataOrNull()).isEqualTo(VALUE)
    }

    @Test
    fun `getDataOrNull should return null if instance is Error`() {
        val result: Result<Int> = Result.Error(EXCEPTION)
        assertThat(result.getDataOrNull()).isNull()
    }

    @Test
    fun `asErrorOrThrow should return error if instance is Error`() {
        assertThat(Result.Error(EXCEPTION).asErrorOrThrow()).isEqualTo(Result.Error(EXCEPTION))
    }

    @Test
    fun `asErrorOrThrow should throw exception if instance is Success`() {
        assertThrows(ClassCastException::class.java) {
            Result.Success(VALUE).asErrorOrThrow()
        }
    }

    @Test
    fun `getExceptionOrThrow should return exception if instance is Error`() {
        assertThat(Result.Error(EXCEPTION).getExceptionOrThrow()).isSameInstanceAs(EXCEPTION)
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