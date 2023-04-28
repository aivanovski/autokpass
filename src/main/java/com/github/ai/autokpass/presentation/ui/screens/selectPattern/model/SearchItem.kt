package com.github.ai.autokpass.presentation.ui.screens.selectPattern.model

import com.github.ai.autokpass.model.AutotypePattern

data class SearchItem(
    val pattern: AutotypePattern,
    val text: String,
    val highlights: List<Int>
)