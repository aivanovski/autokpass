package com.github.ai.autokpass.domain.selector

interface OptionSelector {
    fun select(options: List<String>): Int?
}