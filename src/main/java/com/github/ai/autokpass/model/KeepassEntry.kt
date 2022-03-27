package com.github.ai.autokpass.model

import java.util.UUID

data class KeepassEntry(
    val uid: UUID,
    val title: String,
    val username: String,
    val password: String
) {

    fun isNotEmpty(): Boolean = !isEmpty()

    fun isEmpty(): Boolean {
        return title.isEmpty() && username.isEmpty() && password.isEmpty()
    }
}