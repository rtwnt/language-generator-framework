package net.reusingthewheel.languageGeneratorFramework.soundChange

import java.util.HashSet

/**
 * A state that provides transitions to other states without consuming a symbol.
 */
class EpsilonTransitionState: State() {
    private val transitions = mutableListOf<State>()

    override val isFinal: Boolean
        get() = transitions.isEmpty()

    fun addTransition(state: State) {
        transitions.add(state)
    }

    fun addTransitions(states: List<State>) {
        states.forEach { addTransition(it) }
    }

    override fun getMatchResultsForAllPaths(symbols: List<String>): List<MatchResult> {
        return transitions.map { it.getFirstMatchingPrefix(symbols) }
    }

    override fun generateAllSymbolSequnecesMatchingThisAndFollowingStates(): List<List<String>> {
        return transitions.flatMap { it.generateAllMatchingSymbolSequences() }
    }
}