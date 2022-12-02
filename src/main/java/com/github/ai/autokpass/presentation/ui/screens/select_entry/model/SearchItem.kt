package com.github.ai.autokpass.presentation.ui.screens.select_entry.model

import com.github.ai.autokpass.model.KeepassEntry

data class SearchItem(
    val entry: KeepassEntry,
    val text: String,
    val highlights: List<Int>
)