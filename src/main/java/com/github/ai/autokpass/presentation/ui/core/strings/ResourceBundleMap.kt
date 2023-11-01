package com.github.ai.autokpass.presentation.ui.core.strings

import java.util.ResourceBundle

class ResourceBundleMap(
    private val resources: ResourceBundle
) : Map<String, String> {

    override val entries: Set<Map.Entry<String, String>>
        get() = throw NotImplementedError()

    override val keys: Set<String>
        get() = throw NotImplementedError()

    override val size: Int
        get() = throw NotImplementedError()

    override val values: Collection<String>
        get() = throw NotImplementedError()

    override fun containsKey(key: String): Boolean = throw NotImplementedError()

    override fun containsValue(value: String): Boolean = throw NotImplementedError()

    override fun isEmpty(): Boolean = throw NotImplementedError()

    override fun get(key: String): String? = resources.getString(key)
}