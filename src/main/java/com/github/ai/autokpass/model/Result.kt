package com.github.ai.autokpass.model

sealed class Result<out T : Any?> {

    data class Success<out T : Any?>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    fun getDataOrThrow(): T = (this as Success).data

    fun getErrorOrThrow(): Error = (this as Error)

    fun isFailed(): Boolean = (this is Error)
}
