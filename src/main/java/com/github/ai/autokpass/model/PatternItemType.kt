package com.github.ai.autokpass.model

enum class PatternItemType {
    USERNAME,
    PASSWORD,
    ENTER,
    TAB;

    companion object {
        fun getByName(name: String): PatternItemType? {
            return values().firstOrNull { it.name == name }
        }
    }
}