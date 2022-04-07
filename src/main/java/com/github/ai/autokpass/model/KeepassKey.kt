package com.github.ai.autokpass.model

import java.io.File

sealed class KeepassKey {
    data class PasswordKey(val password: String) : KeepassKey()
    data class FileKey(val file: File) : KeepassKey()
    data class XmlFileKey(val file: File) : KeepassKey()
}