package net.reusingthewheel.languageGeneratorFramework.soundChange

/**
 * A state that requires consuming a symbol to transition to another state from it.
 */
class SymbolConsumingState(private val symbol: String, private val nextState: State) : State() {
    override val isFinal = false

    override fun getMatchResultsForAllPaths(symbols: List<String>, currentIndex: Int): List<MatchResult> {
        var nextResult = MatchResult()
        val currentSymbol = symbols.getOrNull(currentIndex) ?: return listOf(nextResult)
        if (currentSymbol == symbol) {
            nextResult = nextState.getFirstMatch(symbols, currentIndex + 1)
            nextResult.prependMatchingSymbol(currentSymbol)
        }
        return listOf(nextResult)
    }

    override fun generateAllSymbolSequnecesMatchingThisAndFollowingStates(): List<List<String>> {
        return nextState.generateAllMatchingSymbolSequences()
                .map {
                    val result = it.toMutableList()
                    result.add(0, symbol)
                    result
                }
    }
}