package com.github.ai.autokpass.presentation.ui.screens.selectEntry

import com.github.ai.autokpass.model.KeepassKey

data class SelectEntryArgs(
    val key: KeepassKey,
    val filePath: String
)