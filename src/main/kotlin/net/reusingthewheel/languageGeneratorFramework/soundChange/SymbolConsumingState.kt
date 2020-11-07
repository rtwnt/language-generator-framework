package net.reusingthewheel.languageGeneratorFramework.soundChange

class SymbolConsumingState(private val symbol: String, private val nextState: State) : State() {
    override val isFinal = false

    /**
     * Get symbol transition as a map of symbol to the next state
     *
     * @return the map
     */
    override fun getSymbolTransitions(): Map<String, State> {
        return mapOf(symbol to nextState)
    }
}