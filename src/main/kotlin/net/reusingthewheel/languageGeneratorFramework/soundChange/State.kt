package net.reusingthewheel.languageGeneratorFramework.soundChange

import java.util.*

/**
 * A state in a finite state automaton.
 */
internal class State {
    private val symbolTransitions: MutableMap<String, State>
    private val emptySymbolTransitions: MutableSet<State>

    init {
        symbolTransitions = HashMap()
        emptySymbolTransitions = HashSet()
    }

    /**
     * Add a symbol-based transition to another state.
     *
     * @param symbol a symbol that needs to be consumed to move from this state to the next state.
     * @param to the next state for given symbol.
     */
    fun addSymbolTransitions(symbol: String, to: State) {
        symbolTransitions[symbol] = to
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
    val isFinal: Boolean
        get() = symbolTransitions.isEmpty() && emptySymbolTransitions.isEmpty()

    fun getSymbolTransitions(): Map<String, State> {
        return symbolTransitions
    }

    fun getEmptySymbolTransitions(): Set<State> {
        return emptySymbolTransitions
    }
}