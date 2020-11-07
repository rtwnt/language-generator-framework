package net.reusingthewheel.languageGeneratorFramework.soundChange

import java.util.*

/**
 * A state in a finite state automaton.
 */
open class State {
    private val emptySymbolTransitions: MutableSet<State>

    init {
        emptySymbolTransitions = HashSet()
    }

    /**
     * Add an empty symbol-based transition to another state.
     *
     * @param to the next state.
     */
    fun addEmptySymbolTransition(to: State) {
        emptySymbolTransitions.add(to)
    }

    /**
     * Check if the state is a final one of an automaton it occurs in.
     *
     * @return true if it is a final state.
     */
    open val isFinal: Boolean
        get() = emptySymbolTransitions.isEmpty()

    open fun getSymbolTransitions(): Map<String, State> {
        return mapOf()
    }

    fun getEmptySymbolTransitions(): Set<State> {
        return emptySymbolTransitions
    }
}