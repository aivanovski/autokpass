package com.github.ai.autokpass.domain.fuzzy_search

import com.github.ai.autokpass.model.MatcherResult
import de.gesundkrank.fzf4j.matchers.FuzzyMatcherV1
import de.gesundkrank.fzf4j.models.OrderBy

class Fzf4jFuzzyMatcher : FuzzyMatcher {

    override fun <T> match(titles: List<String>, entries: List<T>, query: String): List<MatcherResult<T>> {
        return FuzzyMatcherV1(titles, OrderBy.SCORE, false, false)
            .match(query)
            .map { result ->
                MatcherResult(
                    entry = entries[result.itemIndex],
                    title = titles[result.itemIndex],
                    highlights = result.positions.toList()
                )
            }
    }
}