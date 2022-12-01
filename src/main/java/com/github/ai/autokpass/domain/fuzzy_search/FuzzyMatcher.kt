package com.github.ai.autokpass.domain.fuzzy_search

import com.github.ai.autokpass.model.MatcherResult

interface FuzzyMatcher {
    fun <T> match(
        titles: List<String>,
        entries: List<T>,
        query: String
    ): List<MatcherResult<T>>
}