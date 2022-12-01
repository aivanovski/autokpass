package com.github.ai.autokpass.presentation.ui

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.github.ai.autokpass.presentation.ui.screens.select_entry.SelectEntryArgs
import com.github.ai.autokpass.presentation.ui.screens.select_pattern.SelectPatternArgs

sealed class Screen : Parcelable {

    @Parcelize
    object Unlock : Screen()

    @Parcelize
    data class SelectEntry(
        val args: SelectEntryArgs
    ) : Screen()

    @Parcelize
    data class SelectPattern(
        val args: SelectPatternArgs
    ) : Screen()
}