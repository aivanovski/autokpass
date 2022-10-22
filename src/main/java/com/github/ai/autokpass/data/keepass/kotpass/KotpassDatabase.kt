package com.github.ai.autokpass.data.keepass.kotpass

import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.github.anvell.kotpass.constants.GroupOverride
import io.github.anvell.kotpass.database.KeePassDatabase
import io.github.anvell.kotpass.database.getEntries
import io.github.anvell.kotpass.database.getGroupBy
import io.github.anvell.kotpass.models.Entry
import io.github.anvell.kotpass.models.Group
import java.util.UUID

class KotpassDatabase(
    private val db: KeePassDatabase
) : KeepassDatabase {

    override fun getAllEntries(): List<KeepassEntry> {
        return db
            .getEntries { true }
            .flatMap { (group, entries) -> entries.toKeepassEntries(group) }
    }

    private fun List<Entry>.toKeepassEntries(group: Group): List<KeepassEntry> {
        return map { it.toKeepassEntry(group.isAutotypeEnabled()) }
    }

    private fun Entry.toKeepassEntry(isAutotypeEnabled: Boolean): KeepassEntry {
        val title = fields[FIELD_TITLE]?.content
        val username = fields[FIELD_USER_NAME]?.content
        val password = fields[FIELD_PASSWORD]?.content

        return KeepassEntry(
            uid = uuid,
            title = title ?: EMPTY,
            username = username ?: EMPTY,
            password = password ?: EMPTY,
            isAutotypeEnabled = isAutotypeEnabled
        )
    }

    private fun Group.isAutotypeEnabled(): Boolean {
        var enableAutoType: GroupOverride? = this.enableAutoType
        var currentUid: UUID? = this.uuid

        while (enableAutoType == GroupOverride.Inherit) {
            val parent = currentUid?.let { db.getParentForGroup(it) }

            enableAutoType = parent?.enableAutoType ?: GroupOverride.Enabled
            currentUid = parent?.uuid
        }

        return enableAutoType == GroupOverride.Enabled
    }

    private fun KeePassDatabase.getParentForGroup(groupUid: UUID): Group? {
        return getGroupBy {
            groups.any { child -> child.uuid == groupUid }
        }
    }

    companion object {
        private const val FIELD_USER_NAME = "UserName"
        private const val FIELD_PASSWORD = "Password"
        private const val FIELD_TITLE = "Title"
    }
}