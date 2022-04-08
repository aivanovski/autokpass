package com.github.ai.autokpass.domain.autotype

import com.github.ai.autokpass.model.AutotypePattern.Companion.DEFAULT_PATTERN
import com.github.ai.autokpass.model.AutotypePattern.Companion.PASSWORD
import com.github.ai.autokpass.model.AutotypePattern.Companion.PASSWORD_WITH_ENTER
import com.github.ai.autokpass.model.AutotypePattern.Companion.USERNAME
import com.github.ai.autokpass.model.AutotypePattern.Companion.USERNAME_WITH_ENTER
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AutotypePatternFormatterTest {

    @Test
    fun `format should convert pattern to string`() {
        val formatter = AutotypePatternFormatter()
        assertThat(formatter.format(DEFAULT_PATTERN)).isEqualTo("{USERNAME}{TAB}{PASSWORD}{ENTER}")
        assertThat(formatter.format(USERNAME_WITH_ENTER)).isEqualTo("{USERNAME}{ENTER}")
        assertThat(formatter.format(PASSWORD_WITH_ENTER)).isEqualTo("{PASSWORD}{ENTER}")
        assertThat(formatter.format(USERNAME)).isEqualTo("{USERNAME}")
        assertThat(formatter.format(PASSWORD)).isEqualTo("{PASSWORD}")
    }
}