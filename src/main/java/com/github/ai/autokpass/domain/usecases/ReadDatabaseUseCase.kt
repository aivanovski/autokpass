package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.data.keepass.KeepassDatabaseFactoryProvider
import com.github.ai.autokpass.model.KeepassImplementation
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result

class ReadDatabaseUseCase(
    private val dbFactoryProvider: KeepassDatabaseFactoryProvider
) {

    fun readDatabase(key: KeepassKey, filePath: String): Result<KeepassDatabase> {
        val dbFactory = dbFactoryProvider.getFactory(KeepassImplementation.KEEPASS_JAVA_2)
        return dbFactory.open(key, filePath)
    }
}