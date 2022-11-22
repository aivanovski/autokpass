package com.github.ai.autokpass.model

import java.io.File

sealed class KeepassKey {

    data class PasswordKey(val password: String) : KeepassKey()

    data class FileKey(
        val file: File,
        val processingCommand: String? = null
    ) : KeepassKey()
}