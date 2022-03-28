package com.github.ai.autokpass.domain.selector

import de.gesundkrank.fzf4j.Fzf
import de.gesundkrank.fzf4j.models.OrderBy

class Fzf4jOptionSelector : OptionSelector {

    private val fzf: Fzf by lazy {
        Fzf.builder()
            .reverse()
            .orderBy(OrderBy.SCORE)
            .normalize()
            .build()
    }

    override fun select(options: List<String>): Int? {
        val selectedOption = try {
            fzf.select(options)
        } catch (e: Fzf.AbortByUserException) {
            null
        }

        return if (selectedOption != null) {
            val idx = options.indexOf(selectedOption)
            if (idx != -1) {
                idx
            } else {
                null
            }
        } else {
            null
        }
    }
}