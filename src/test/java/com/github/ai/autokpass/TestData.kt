package com.github.ai.autokpass

import com.github.ai.autokpass.model.KeepassEntry
import com.github.ai.autokpass.utils.resourceAsStream
import org.linguafranca.pwdb.Credentials
import org.linguafranca.pwdb.kdbx.KdbxCreds
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase
import java.io.InputStream
import java.util.UUID

object TestData {

    const val DB_PASSWORD = "abc123"
    const val DB_PATH = "/path/db.kdbx"

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

    val ENTRIES = listOf(ENTRY1, ENTRY2)

    val DB_WITH_PASSWORD = TestDatabase(
        filename = "db-with-password.kdbx",
        key = TestKey.PasswordKey("abc123"),
        entries = listOf(
            KeepassEntry(
                uid = UUID.fromString("9d04bfed-b452-b903-a627-63079425c6d0"),
                title = "this is a test",
                username = "abc",
                password = "123"
            ),
            KeepassEntry(
                uid = UUID.fromString("832dde58-ae77-8e9b-9848-8cacc8ef1f8d"),
                title = "username hs required word",
                username = "test@example.com",
                password = "123"
            ),
            KeepassEntry(
                uid = UUID.fromString("df0eb0e3-0901-2332-85b1-df146d7bf707"),
                title = "notes contains required",
                username = "something",
                password = "123"
            ),
            KeepassEntry(
                uid = UUID.fromString("bb839524-f20f-6405-8b3a-e09d3c7438b3"),
                title = "url has required word",
                username = "jjj",
                password = "123"
            ),
            KeepassEntry(
                uid = UUID.fromString("90bc6fd8-cb13-1891-3d2a-042ef8143edc"),
                title = "p'word has the required word",
                username = "something",
                password = "test"
            ),
            KeepassEntry(
                uid = UUID.fromString("af1aea21-cdad-6c34-9c76-bfac0e22852e"),
                title = "password1",
                username = "",
                password = "password1"
            ),
            KeepassEntry(
                uid = UUID.fromString("6e6b63d9-ad9b-cf71-b058-74c24d262986"),
                title = "password with spaces",
                username = "",
                password = "password with spaces"
            ),
            KeepassEntry(
                uid = UUID.fromString("5a133463-5962-6045-0792-c2304ce28d18"),
                title = "password with accents àéç",
                username = "",
                password = "password with accents àéç"
            ),
            KeepassEntry(
                uid = UUID.fromString("54b82b71-f5c7-a6a4-5921-b9fc624d6564"),
                title = "password non-Latin יוֹסֵף",
                username = "",
                password = "password non-Latin יוֹסֵף"
            )
        )
    )

    val DB_WITH_BINARY_KEY = TestDatabase(
        filename = "db-with-bin-key.kdbx",
        key = TestKey.FileKey("bin-key"),
        entries = listOf(
            KeepassEntry(
                uid = UUID.fromString("7d47af7f-261b-cd84-248b-3da03209119e"),
                title = "Some entry",
                username = "user",
                password = "password"
            ),
            KeepassEntry(
                uid = UUID.fromString("dc4aedff-cdc5-e01e-b9e9-cf92fd05ee86"),
                title = "Email",
                username = "",
                password = ""
            ),
            KeepassEntry(
                uid = UUID.fromString("4f4bef2a-a085-bcee-f035-60f4975c1ca8"),
                title = "Wi-Fi",
                username = "",
                password = ""
            ),
            KeepassEntry(
                uid = UUID.fromString("5847ccc4-b400-803b-2a10-5ec24518929c"),
                title = "Notes",
                username = "",
                password = ""
            ),
            KeepassEntry(
                uid = UUID.fromString("78408145-7c84-8f54-71bc-d4e51a152aa5"),
                title = "ID Card",
                username = "",
                password = ""
            ),
            KeepassEntry(
                uid = UUID.fromString("6a4bbfca-684f-9f63-4609-8b1b8547c9a5"),
                title = "Debit / Credit Card",
                username = "",
                password = ""
            ),
            KeepassEntry(
                uid = UUID.fromString("20471cd3-885c-5d42-0dc5-f6123bd05e87"),
                title = "Bank",
                username = "",
                password = ""
            ),
            KeepassEntry(
                uid = UUID.fromString("8e40a204-70c6-26b6-ad91-54e566925e95"),
                title = "Cryptocurrency wallet",
                username = "",
                password = ""
            )
        )
    )

    val DB_WITH_XML_KEY = TestDatabase(
        filename = "db-with-xml-key.kdbx",
        key = TestKey.XmlFileKey("xml-key"),
        entries = listOf(
            KeepassEntry(
                uid=UUID.fromString("51fb540e-7f6d-4658-8574-0ad97375188f"),
                title="this is a test",
                username="abc",
                password="123"
            ),
            KeepassEntry(
                uid=UUID.fromString("493a30d2-41b7-4b62-a30b-1450aa269874"),
                title="username hs required word",
                username="test@example.com",
                password="123"
            ),
            KeepassEntry(
                uid=UUID.fromString("4aea2555-ff7f-4258-b997-953f6caa41ec"),
                title="notes contains required",
                username="something",
                password="123"
            ),
            KeepassEntry(
                uid=UUID.fromString("7bb53d99-f864-4154-94ac-5bddc02daded"),
                title="url has required word",
                username="jjj",
                password="123"
            ),
            KeepassEntry(
                uid=UUID.fromString("5989ff5c-6460-4a76-a689-a1d707889ee3"),
                title="p'word has the required word",
                username="something",
                password="test"
            ),
            KeepassEntry(
                uid=UUID.fromString("f356479b-1355-4b4a-ac06-8fd302b72428"),
                title="password1",
                username="",
                password="password1"
            ),
            KeepassEntry(
                uid=UUID.fromString("a25604d8-e5e2-4332-9b3a-9edc755b54ed"),
                title="password with spaces",
                username="",
                password="password with spaces"
            ),
            KeepassEntry(
                uid=UUID.fromString("69804f94-dcf1-4868-9845-854e4c602721"),
                title="password with accents àéç",
                username="",
                password="password with accents àéç"
            ),
            KeepassEntry(
                uid=UUID.fromString("50466561-8fd6-4627-9a5b-f5f5e238dba5"),
                title="password non-Latin יוֹסֵף",
                username="",
                password="password non-Latin יוֹסֵף"
            )
        )
    )

    data class TestDatabase(
        val filename: String,
        val key: TestKey,
        val entries: List<KeepassEntry>
    ) {

        fun asStream(): InputStream = resourceAsStream(filename)

        fun getCredentials(): Credentials {
            return when (key) {
                is TestKey.PasswordKey -> KdbxCreds(key.password.toByteArray())
                is TestKey.FileKey -> KdbxCreds(resourceAsStream(key.filename).readAllBytes())
                is TestKey.XmlFileKey -> KdbxCreds(byteArrayOf(), resourceAsStream(key.filename))
            }
        }

        fun loadDatabase(): SimpleDatabase {
            return SimpleDatabase.load(getCredentials(), resourceAsStream(filename))
        }
    }

    sealed class TestKey {
        data class PasswordKey(val password: String) : TestKey()
        data class FileKey(val filename: String) : TestKey() {
            fun asStream(): InputStream = resourceAsStream(filename)
        }
        data class XmlFileKey(val filename: String) : TestKey() {
            fun asStream(): InputStream = resourceAsStream(filename)
        }
    }
}