package net.reusingthewheel.languageGeneratorFramework.soundChange

import java.util.HashSet

/**
 * A state that provides transitions to other states without consuming a symbol.
 */
class EpsilonTransitionState(private val captureIndexes: Boolean = false): State() {
    private val transitions = mutableListOf<State>()

    override val isFinal: Boolean
        get() = transitions.isEmpty()

    fun addTransition(state: State) {
        transitions.add(state)
    }

    fun addTransitions(states: List<State>) {
        states.forEach { addTransition(it) }
    }

    override fun getMatchResultsForAllPaths(symbols: List<String>, currentIndex: Int): List<MatchResult> {
        val result = transitions.map { it.getFirstMatchingPrefix(symbols, currentIndex) }
        if (captureIndexes) {
            result.map { it.capturedIndexes.add(currentIndex) }
        }
        return result
    }

    override fun generateAllSymbolSequnecesMatchingThisAndFollowingStates(): List<List<String>> {
        return transitions.flatMap { it.generateAllMatchingSymbolSequences() }
    }
}