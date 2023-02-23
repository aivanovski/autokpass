package com.github.ai.autokpass.data.keepass

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.data.keepass.kotpass.KotpassDatabaseFactory
import com.github.ai.autokpass.model.KeepassImplementation
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources

class KeepassDatabaseFactoryProvider(
    private val fileSystemProvider: FileSystemProvider,
    private val processExecutor: ProcessExecutor,
    private val strings: StringResources
) {

    fun getFactory(type: KeepassImplementation): KeepassDatabaseFactory {
        return when (type) {
            KeepassImplementation.KOTPASS -> KotpassDatabaseFactory(
                fileSystemProvider,
                processExecutor,
                strings
            )
        }
    }
}