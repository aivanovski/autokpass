package com.github.ai.autokpass.data.keepass

import com.github.ai.autokpass.model.KeepassImplementation
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Test

class KeepassDatabaseFactoryProviderTest {

    @Test
    fun `getFactory should return factory for KeePassJava2`() {
        val factory = KeepassDatabaseFactoryProvider(mockk())
            .getFactory(KeepassImplementation.KEEPASS_JAVA_2)

        assertThat(factory).isInstanceOf(KeepassDatabaseFactory::class.java)
    }
}