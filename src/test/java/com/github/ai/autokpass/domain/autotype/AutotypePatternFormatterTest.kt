package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypePattern.Companion.DEFAULT_PATTERN
import com.github.ai.autokpass.model.AutotypePattern.Companion.PASSWORD
import com.github.ai.autokpass.model.AutotypePattern.Companion.PASSWORD_WITH_ENTER
import com.github.ai.autokpass.model.AutotypePattern.Companion.USERNAME
import com.github.ai.autokpass.model.AutotypePattern.Companion.USERNAME_WITH_ENTER
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AutotypePatternFormatterTest {

    @Test
    fun `format should convert pattern to string`() {
        with(AutotypePatternFormatter()) {
            format(DEFAULT_PATTERN) shouldBe "{USERNAME}{TAB}{PASSWORD}{ENTER}"
            format(USERNAME_WITH_ENTER) shouldBe "{USERNAME}{ENTER}"
            format(PASSWORD_WITH_ENTER) shouldBe "{PASSWORD}{ENTER}"
            format(USERNAME) shouldBe "{USERNAME}"
            format(PASSWORD) shouldBe "{PASSWORD}"
        }
    }
}