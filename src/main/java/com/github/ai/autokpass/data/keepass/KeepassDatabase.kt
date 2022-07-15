package com.github.ai.autokpass.data.keepass

import com.github.ai.autokpass.model.KeepassEntry

interface KeepassDatabase {

    fun getAllEntries(): List<KeepassEntry>
}