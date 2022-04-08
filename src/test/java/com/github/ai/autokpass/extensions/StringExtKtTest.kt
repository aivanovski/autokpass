package com.github.ai.autokpass.extensions

import com.github.ai.autokpass.util.StringUtils
import com.github.ai.autokpass.util.StringUtils.EMPTY
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringExtKtTest {

    @Test
    fun `toIntSafely should parse integers`() {
        assertThat("1".toIntSafely()).isEqualTo(1)
        assertThat("-1".toIntSafely()).isEqualTo(-1)
        assertThat("${Integer.MAX_VALUE}".toIntSafely()).isEqualTo(Integer.MAX_VALUE)
        assertThat("${Integer.MIN_VALUE}".toIntSafely()).isEqualTo(Integer.MIN_VALUE)
    }

    @Test
    fun `toIntSafely should return null`() {
        assertThat("${Long.MAX_VALUE}".toIntSafely()).isNull()
        assertThat("-".toIntSafely()).isNull()
        assertThat("123-".toIntSafely()).isNull()
        assertThat("123abc".toIntSafely()).isNull()
    }

    @Test
    fun `splitIntoCommandAndArgs should split command by word`() {
        "command --arg1 value1 --arg2".apply {
            val (command, args) = this.splitIntoCommandAndArgs()
            assertThat(command).isEqualTo("command")
            assertThat(args).isEqualTo(listOf("--arg1", "value1", "--arg2"))
        }
    }

    @Test
    fun `splitIntoCommandAndArgs should return command itself`() {
        "command".apply {
            val (command, args) = this.splitIntoCommandAndArgs()
            assertThat(command).isEqualTo("command")
            assertThat(args).isEqualTo(emptyList<String>())
        }
        "".apply {
            val (command, args) = this.splitIntoCommandAndArgs()
            assertThat(command).isEqualTo("")
            assertThat(args).isEqualTo(emptyList<String>())
        }
    }

    @Test
    fun `maskSymbolWith should mask all characters with specified character`() {
        assertThat("abc123".maskSymbolsWith('*')).isEqualTo("******")
        assertThat(EMPTY.maskSymbolsWith('*')).isEqualTo(EMPTY)
    }
}