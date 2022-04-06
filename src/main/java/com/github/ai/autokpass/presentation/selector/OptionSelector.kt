package com.github.ai.autokpass.presentation.selector

import com.github.ai.autokpass.model.Result

interface OptionSelector {
    fun select(options: List<String>): Result<Int>
}