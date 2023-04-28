package com.github.ai.autokpass.extensions

import com.github.ai.autokpass.util.StringUtils.EMPTY
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class StringExtKtTest {

    @Test
    fun `toIntSafely should parse integers`() {
        "1".toIntSafely() shouldBe 1
        "-1".toIntSafely() shouldBe -1
        "${Integer.MAX_VALUE}".toIntSafely() shouldBe Integer.MAX_VALUE
        "${Integer.MIN_VALUE}".toIntSafely() shouldBe Integer.MIN_VALUE
    }

    @Test
    fun `toIntSafely should return null`() {
        "${Long.MAX_VALUE}".toIntSafely() should beNull()
        "-".toIntSafely() should beNull()
        "123-".toIntSafely() should beNull()
        "123abc".toIntSafely() should beNull()
    }

    @Test
    fun `splitIntoCommandAndArgs should split command by word`() {
        "command --arg1 value1 --arg2".apply {
            val (command, args) = this.splitIntoCommandAndArgs()
            command shouldBe "command"
            args shouldBe listOf("--arg1", "value1", "--arg2")
        }
    }

    @Test
    fun `splitIntoCommandAndArgs should return command itself`() {
        "command".apply {
            val (command, args) = this.splitIntoCommandAndArgs()
            command shouldBe "command"
            args shouldBe emptyList()
        }
        "".apply {
            val (command, args) = this.splitIntoCommandAndArgs()
            command shouldBe EMPTY
            args shouldBe emptyList()
        }
    }

    @Test
    fun `maskSymbolWith should mask all characters with specified character`() {
        "abc123".maskSymbolsWith('*') shouldBe "******"
        EMPTY.maskSymbolsWith('*') shouldBe EMPTY
    }
}