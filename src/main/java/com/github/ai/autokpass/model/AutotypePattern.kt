package com.github.ai.autokpass.model

data class AutotypePattern(
    val items: List<PatternItemType>
) {

    companion object {
        val DEFAULT_PATTERN = AutotypePattern(
            listOf(
                PatternItemType.USERNAME,
                PatternItemType.TAB,
                PatternItemType.PASSWORD,
                PatternItemType.ENTER
            )
        )
        val USERNAME_WITH_ENTER = AutotypePattern(
            listOf(
                PatternItemType.USERNAME,
                PatternItemType.ENTER
            )
        )
        val PASSWORD_WITH_ENTER = AutotypePattern(
            listOf(
                PatternItemType.PASSWORD,
                PatternItemType.ENTER
            )
        )
        val USERNAME = AutotypePattern(
            listOf(
                PatternItemType.USERNAME
            )
        )
        val PASSWORD = AutotypePattern(
            listOf(
                PatternItemType.PASSWORD
            )
        )
    }
}