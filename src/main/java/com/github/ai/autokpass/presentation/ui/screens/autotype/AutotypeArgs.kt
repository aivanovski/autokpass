package com.github.ai.autokpass.presentation.ui.screens.autotype

import com.github.ai.autokpass.model.AutotypePattern
import com.github.ai.autokpass.model.KeepassEntry

data class AutotypeArgs(
    val entry: KeepassEntry,
    val pattern: AutotypePattern
)