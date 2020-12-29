package net.reusingthewheel.languageGeneratorFramework.soundChange


/**
 * A result of matching method called on an automaton
 */
class MatchResult {
    var isMatchDetected = false
    val matchedSymbols = mutableListOf<String>()
    val capturedIndexes = mutableListOf<Int>()
    fun prependMatchingSymbol(symbol: String) {
        matchedSymbols.add(0, symbol)
    }
}