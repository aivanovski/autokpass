package com.github.ai.autokpass.presentation.selector

interface OptionSelector {
    fun select(options: List<String>): Int?
}