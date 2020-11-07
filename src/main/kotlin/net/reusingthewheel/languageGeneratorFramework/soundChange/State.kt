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

    fun getLongestMatchingPrefix(symbols: List<String>): MatchResult {
        val result = MatchResult()
        if (isFinal) {
            result.isMatchDetected = true
            return result
        }
        return getMatchResultsForAllPaths(symbols)
                .filter(MatchResult::isMatchDetected)
                .maxByOrNull { it.matchedSymbols.size } ?: result
    }

    abstract fun getMatchResultsForAllPaths(symbols: List<String>): List<MatchResult>
}