package com.github.ai.autokpass.data.keepass.kotpass

import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.database.getEntries
import io.github.anvell.kotpass.models.Entry

class KotpassDatabase(
    private val db: KeePassDatabase
) : KeepassDatabase {

    override fun getAllEntries(): List<KeepassEntry> {
        return db
            .getEntries { true }
            .flatMap { pair -> pair.second }
            .toKeepassEntries()
    }

    private fun List<Entry>.toKeepassEntries(): List<KeepassEntry> {
        return map { it.toKeepassEntry() }
    }

    private fun Entry.toKeepassEntry(): KeepassEntry {
        val title = fields[FIELD_TITLE]?.content
        val username = fields[FIELD_USER_NAME]?.content
        val password = fields[FIELD_PASSWORD]?.content

        return KeepassEntry(
            uid = uuid,
            title = title ?: EMPTY,
            username = username ?: EMPTY,
            password = password ?: EMPTY
        )
    }

    companion object {
        private const val FIELD_USER_NAME = "UserName"
        private const val FIELD_PASSWORD = "Password"
        private const val FIELD_TITLE = "Title"
    }
}