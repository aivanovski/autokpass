package com.github.ai.autokpass.data.keepass

import com.github.ai.autokpass.data.keepass.kotpass.KotpassDatabaseFactory
import com.github.ai.autokpass.model.KeepassImplementation
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Test

class KeepassDatabaseFactoryProviderTest {

    @Test
    fun `getFactory should return factor`() {
        val factory = KeepassDatabaseFactoryProvider(mockk())
            .getFactory(KeepassImplementation.KOTPASS)

        assertThat(factory).isInstanceOf(KotpassDatabaseFactory::class.java)
    }
}