package com.github.ai.autokpass.extensions

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
}