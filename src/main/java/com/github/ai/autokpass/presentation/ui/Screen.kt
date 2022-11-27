package com.github.ai.autokpass.presentation.ui

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.github.ai.autokpass.model.KeepassKey

sealed class Screen : Parcelable {

    @Parcelize
    object Unlock : Screen()

    @Parcelize
    data class SelectEntry(val key: KeepassKey) : Screen()
}