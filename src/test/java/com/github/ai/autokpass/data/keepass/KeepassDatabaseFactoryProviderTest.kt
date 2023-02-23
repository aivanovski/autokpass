package com.github.ai.autokpass.data.keepass

import com.github.ai.autokpass.data.keepass.kotpass.KotpassDatabaseFactory
import com.github.ai.autokpass.model.KeepassImplementation
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import io.mockk.mockk
import org.junit.jupiter.api.Test

class KeepassDatabaseFactoryProviderTest {

    @Test
    fun `getFactory should return Kotpass factory`() {
        val factory = KeepassDatabaseFactoryProvider(mockk(), mockk(), mockk())
            .getFactory(KeepassImplementation.KOTPASS)

        factory should beInstanceOf<KotpassDatabaseFactory>()
    }
}