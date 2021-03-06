package com.github.ai.autokpass.data.keepass

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.data.keepass.kotpass.KotpassDatabaseFactory
import com.github.ai.autokpass.model.KeepassImplementation

class KeepassDatabaseFactoryProvider(
    private val fileSystemProvider: FileSystemProvider
) {

    fun getFactory(type: KeepassImplementation): KeepassDatabaseFactory {
        return when (type) {
            KeepassImplementation.KOTPASS -> KotpassDatabaseFactory(fileSystemProvider)
        }
    }
}