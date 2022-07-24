package com.github.ai.autokpass.utils

import com.github.ai.autokpass.model.Result
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertAbout

class ResultSubject private constructor(
    metadata: FailureMetadata,
    private val actual: Result<*>
) : Subject(metadata, actual) {

    fun isSuccessful() {
        Truth.assertThat(actual).isInstanceOf(Result.Success::class.java)
    }

    fun isFailed() {
        Truth.assertThat(actual).isInstanceOf(Result.Error::class.java)
    }

    fun hasDataEqualTo(expected: Any) {
        isSuccessful()
        Truth.assertThat(actual.getDataOrThrow()).isEqualTo(expected)
    }

    fun hasException(expectedType: Class<*>) {
        isFailed()

        val actualException = actual.asErrorOrThrow().exception
        Truth.assertThat(actualException).isInstanceOf(expectedType)
    }

    fun hasErrorMessage(expectedMessage: String) {
        isFailed()

        val actualMessage = actual.asErrorOrThrow().exception.message
        Truth.assertThat(actualMessage).isEqualTo(expectedMessage)
    }

    companion object {

        private val ERROR_SUBJECT_FACTORY = Factory<ResultSubject, Result<*>> { metadata, actual ->
            ResultSubject(metadata, actual)
        }

        fun assertThat(actual: Result<*>): ResultSubject {
            return assertAbout(ERROR_SUBJECT_FACTORY).that(actual)
        }
    }
}