package com.github.ai.autokpass.model

data class AutotypePattern(
    val items: List<AutotypeItemType>
) {

    companion object {
        val DEFAULT_PATTERN = AutotypePattern(
            listOf(
                AutotypeItemType.USERNAME,
                AutotypeItemType.TAB,
                AutotypeItemType.PASSWORD,
                AutotypeItemType.ENTER
            )
        )
    }
}