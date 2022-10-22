package com.github.ai.autokpass

import com.github.ai.autokpass.model.AutotypeSequenceItem
import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.utils.resourceAsStream
import java.io.InputStream
import java.util.UUID

object TestData {

    const val INVALID_DB_PASSWORD = "123456"
    const val DB_PASSWORD = "abc123"
    const val DB_PATH = "/path/db.kdbx"
    const val DEFAULT_DELAY = 200L
    const val DEFAULT_INPUT_TEXT = "abc123"

    val ENTRY1 = KeepassEntry(
        uid = UUID.fromString("11111111-2222-3333-4444-555555555555"),
        title = "title1",
        username = "username1",
        password = "password1",
        isAutotypeEnabled = true
    )

    val ENTRY2 = KeepassEntry(
        uid = UUID.fromString("22222222-2222-3333-4444-555555555555"),
        title = "title2",
        username = "username2",
        password = "password2",
        isAutotypeEnabled = false
    )

    val DEFAULT_AUTOTYPE_ITEMS = listOf(
        AutotypeSequenceItem.Text(DEFAULT_INPUT_TEXT),
        AutotypeSequenceItem.Tab,
        AutotypeSequenceItem.Enter,
        AutotypeSequenceItem.Delay(DEFAULT_DELAY)
    )

    val ENTRIES = listOf(ENTRY1, ENTRY2)

    private val TEST_DB_ENTRIES = listOf(
        KeepassEntry(
            uid = UUID.fromString("832dde58-ae77-8e9b-9848-8cacc8ef1f8d"),
            title = "username hs required word",
            username = "test@example.com",
            password = "123",
            isAutotypeEnabled = true
        ),
        KeepassEntry(
            uid = UUID.fromString("90bc6fd8-cb13-1891-3d2a-042ef8143edc"),
            title = "p'word has the required word",
            username = "something",
            password = "test",
            isAutotypeEnabled = true
        ),
        KeepassEntry(
            uid = UUID.fromString("9d04bfed-b452-b903-a627-63079425c6d0"),
            title = "this is a test",
            username = "abc",
            password = "123",
            isAutotypeEnabled = true
        ),
        KeepassEntry(
            uid = UUID.fromString("bb839524-f20f-6405-8b3a-e09d3c7438b3"),
            title = "url has required word",
            username = "jjj",
            password = "123",
            isAutotypeEnabled = true
        ),
        KeepassEntry(
            uid = UUID.fromString("df0eb0e3-0901-2332-85b1-df146d7bf707"),
            title = "notes contains required",
            username = "something",
            password = "123",
            isAutotypeEnabled = true
        ),
        KeepassEntry(
            uid = UUID.fromString("54b82b71-f5c7-a6a4-5921-b9fc624d6564"),
            title = "password non-Latin יוֹסֵף",
            username = "",
            password = "password non-Latin יוֹסֵף",
            isAutotypeEnabled = true
        ),
        KeepassEntry(
            uid = UUID.fromString("5a133463-5962-6045-0792-c2304ce28d18"),
            title = "password with accents àéç",
            username = "",
            password = "password with accents àéç",
            isAutotypeEnabled = true
        ),
        KeepassEntry(
            uid = UUID.fromString("af1aea21-cdad-6c34-9c76-bfac0e22852e"),
            title = "password1",
            username = "",
            password = "password1",
            isAutotypeEnabled = false
        ),
        KeepassEntry(
            uid = UUID.fromString("6e6b63d9-ad9b-cf71-b058-74c24d262986"),
            title = "password with spaces",
            username = "",
            password = "password with spaces",
            isAutotypeEnabled = false
        )
    )

    val DB_WITH_PASSWORD = TestDatabase(
        filename = "db-with-password.kdbx",
        key = TestKey.PasswordKey("abc123"),
        entries = TEST_DB_ENTRIES
    )

    val DB_WITH_BINARY_KEY = TestDatabase(
        filename = "db-with-bin-key.kdbx",
        key = TestKey.FileKey("bin-key"),
        entries = TEST_DB_ENTRIES
    )

    data class TestDatabase(
        val filename: String,
        val key: TestKey,
        val entries: List<KeepassEntry>
    ) {

        fun asStream(): InputStream = resourceAsStream(filename)
    }

    sealed class TestKey {
        data class PasswordKey(val password: String) : TestKey()
        data class FileKey(val filename: String) : TestKey() {
            fun asStream(): InputStream = resourceAsStream(filename)
        }
    }
}