package com.github.ai.autokpass.presentation.ui

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.github.ai.autokpass.presentation.ui.screens.autotype.AutotypeArgs
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryArgs
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternArgs

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

    @Parcelize
    data class Autotype(
        val args: AutotypeArgs
    ) : Screen()

    @Parcelize
    object Settings : Screen()
}