package net.reusingthewheel.languageGeneratorFramework.soundChange

import java.util.HashSet

/**
 * A state that provides transitions to other states without consuming a symbol.
 */
class EpsilonTransitionState: State() {
    private val transitions: MutableSet<State>

    override val isFinal: Boolean
        get() = transitions.isEmpty()

    init {
        transitions = HashSet()
    }

    fun addTransition(state: State) {
        transitions.add(state)
    }

    override fun getMatchResultsForAllPaths(symbols: List<String>): List<MatchResult> {
        return transitions.map { it.getLongestMatchingPrefix(symbols) }
    }
}