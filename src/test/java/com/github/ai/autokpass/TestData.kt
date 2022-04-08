package com.github.ai.autokpass

import com.github.ai.autokpass.model.KeepassEntry
import java.util.UUID

object TestData {

    val ENTRY1 = KeepassEntry(
        uid = UUID.fromString("11111111-2222-3333-4444-555555555555"),
        title = "title1",
        username = "username1",
        password = "password1"
    )

    val ENTRY2 = KeepassEntry(
        uid = UUID.fromString("22222222-2222-3333-4444-555555555555"),
        title = "title2",
        username = "username2",
        password = "password2"
    )
}