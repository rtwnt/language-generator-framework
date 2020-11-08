package net.reusingthewheel.languageGeneratorFramework.soundChange

/**
 * A state that requires consuming a symbol to transition to another state from it.
 */
class SymbolConsumingState(private val symbol: String, private val nextState: State) : State() {
    override val isFinal = false

    override fun getMatchResultsForAllPaths(symbols: List<String>): List<MatchResult> {
        var nextResult = MatchResult()
        if (symbols.isEmpty()) {
            return listOf(nextResult)
        }
        val currentSymbol = symbols[0]
        if (currentSymbol == symbol) {
            val nextSubsequence = symbols.subList(1, symbols.size)
            nextResult = nextState.getFirstMatchingPrefix(nextSubsequence)
            nextResult.prependMatchingSymbol(currentSymbol)
        }
        return listOf(nextResult)
    }
}