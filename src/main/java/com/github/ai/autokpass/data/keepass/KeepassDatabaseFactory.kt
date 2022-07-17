package com.github.ai.autokpass.data.keepass

import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.Result

interface KeepassDatabaseFactory {

    fun open(key: KeepassKey, filePath: String): Result<KeepassDatabase>
}