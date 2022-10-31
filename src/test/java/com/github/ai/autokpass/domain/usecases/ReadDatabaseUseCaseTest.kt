package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.data.keepass.KeepassDatabase
import com.github.ai.autokpass.data.keepass.KeepassDatabaseFactory
import com.github.ai.autokpass.data.keepass.KeepassDatabaseFactoryProvider
import com.github.ai.autokpass.model.KeepassImplementation
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.model.Result
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class ReadDatabaseUseCaseTest {

    @Test
    fun `readDatabase should call factory`() {
        // arrange
        val factory = mockk<KeepassDatabaseFactory>()
        val factoryProvider = mockk<KeepassDatabaseFactoryProvider>()
        val db = mockk<KeepassDatabase>()
        val key = PasswordKey(DB_PASSWORD)

        every { factoryProvider.getFactory(KeepassImplementation.KOTPASS) }.returns(factory)
        every { factory.open(key, DB_PATH) }.returns(Result.Success(db))

        // act
        val result = ReadDatabaseUseCase(factoryProvider)
            .readDatabase(PasswordKey(DB_PASSWORD), DB_PATH)

        // assert
        verify { factoryProvider.getFactory(KeepassImplementation.KOTPASS) }
        verify { factory.open(key, DB_PATH) }

        result.isSucceeded() shouldBe true
        result.getDataOrThrow() shouldBe db
    }
}