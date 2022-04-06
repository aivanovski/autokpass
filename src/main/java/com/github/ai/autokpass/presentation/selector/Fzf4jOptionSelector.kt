package com.github.ai.autokpass.presentation.selector

import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.model.Result
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

    override fun select(options: List<String>): Result<Int> {
        return try {
            val selectedOption = fzf.select(options)
            val idx = options.indexOf(selectedOption)
            if (idx != -1) {
                Result.Success(idx)
            } else {
                Result.Error(AutokpassException("Error has occurred"))
            }
        } catch (e: Fzf.AbortByUserException) {
            Result.Error(AutokpassException("Cancelled"))
        } catch (e: Fzf.EmptyResultException) {
            Result.Error(AutokpassException("Nothing was selected"))
        }
    }
}