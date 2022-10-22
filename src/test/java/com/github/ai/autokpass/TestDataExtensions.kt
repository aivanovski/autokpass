package com.github.ai.autokpass

import com.github.ai.autokpass.TestData.TestDatabase
import com.github.ai.autokpass.TestData.TestKey
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.utils.resourceAsBytes
import com.github.ai.autokpass.utils.resourceAsStream
import io.github.anvell.kotpass.cryptography.EncryptedValue
import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.database.decode
import java.io.File

fun TestDatabase.loadKotpassDatabase(): KeePassDatabase {
    return KeePassDatabase.decode(resourceAsStream(filename), key.toKotpassCredentials())
}

fun TestDatabase.getFilePath(): String {
    return "/$filename"
}

fun TestKey.toKotpassCredentials(): io.github.anvell.kotpass.database.Credentials {
    return when (this) {
        is TestKey.PasswordKey -> io.github.anvell.kotpass.database.Credentials.from(
            EncryptedValue.fromString(password)
        )
        is TestKey.FileKey -> io.github.anvell.kotpass.database.Credentials.from(
            EncryptedValue.fromBinary(resourceAsBytes(filename))
        )
    }
}

fun TestKey.getPasswordOrThrow(): String {
    return if (this is TestKey.PasswordKey) {
        password
    } else {
        throw IllegalStateException()
    }
}

fun TestKey.getFilePathOrThrow(): String {
    return when (this) {
        is TestKey.FileKey -> getFilePath()
        else -> throw IllegalStateException()
    }
}

fun TestKey.asPasswordKey(): TestKey.PasswordKey {
    return if (this is TestKey.PasswordKey) {
        this
    } else {
        throw IllegalStateException()
    }
}

fun TestKey.asFileKey(): TestKey.FileKey {
    return if (this is TestKey.FileKey) {
        this
    } else {
        throw IllegalStateException()
    }
}

fun TestKey.FileKey.getFilePath(): String = "/$filename"

fun TestKey.toKeepassKey(): KeepassKey {
    return when (this) {
        is TestKey.PasswordKey -> KeepassKey.PasswordKey(password)
        is TestKey.FileKey -> KeepassKey.FileKey(File(getFilePathOrThrow()))
    }
}
