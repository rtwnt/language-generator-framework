package net.reusingthewheel.languageGeneratorFramework.soundChange

/**
 * A state in a finite state automaton.
 */
abstract class State {
    /**
     * Check if the state is a final one of an automaton it occurs in.
     *
     * @return true if it is a final state.
     */
    open val isFinal = false

    fun getFirstMatchingPrefix(symbols: List<String>): MatchResult {
        val result = MatchResult()
        if (isFinal) {
            result.isMatchDetected = true
            return result
        }
        return getMatchResultsForAllPaths(symbols)
                .firstOrNull(MatchResult::isMatchDetected) ?: result
    }

    fun generateAllMatchingSymbolSequences(): List<List<String>> {
        if (isFinal) {
            return listOf(listOf())
        }

        return generateAllSymbolSequnecesMatchingThisAndFollowingStates()
    }

    abstract fun getMatchResultsForAllPaths(symbols: List<String>): List<MatchResult>

    abstract fun generateAllSymbolSequnecesMatchingThisAndFollowingStates(): List<List<String>>
}