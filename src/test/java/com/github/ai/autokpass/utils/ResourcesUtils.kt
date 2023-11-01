package com.github.ai.autokpass.utils

import com.github.ai.autokpass.TestData
import java.io.ByteArrayInputStream
import java.io.InputStream

fun resourceAsBytes(name: String): ByteArray {
    val stream = TestData.javaClass.classLoader.getResourceAsStream(name)
    checkNotNull(stream)

    return stream.readAllBytes()
}

fun resourceAsString(name: String): String =
    String(resourceAsBytes(name))

fun resourceAsStream(name: String): InputStream =
    ByteArrayInputStream(resourceAsBytes(name))