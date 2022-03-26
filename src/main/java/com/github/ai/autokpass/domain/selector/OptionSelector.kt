package com.github.ai.autokpass.domain.selector

interface OptionSelector {
    fun show(options: List<String>): Int?
}