package net.reusingthewheel.languageGeneratorFramework.soundChange


/**
 * A nondeterministic finite state automaton
 */
class NFA private constructor(private val start: State, private val end: State) {

    /**
     * Get the subsequence of given sequence of symbols starting from the beginning of given sequence
     * such as the automaton reaches it's final state after consuming the subsequence.
     *
     * @param symbols a list of symbols.
     * @return MatchResult with information on whether the final state has been reached and the subsequence
     * of symbols consumed in the process.
     */
    fun getMatchingPrefix(symbols: List<String>): MatchResult {
        require(symbols.isNotEmpty()) { "A sequence of symbols cannot be empty" }
        return getMatchingPrefix(symbols, start)
    }

    private fun getMatchingPrefix(symbols: List<String>, currentState: State): MatchResult {
        val result = MatchResult()
        if (currentState.isFinal) {
            result.isMatchDetected = true
            return result
        }

        val allMatchResults = mutableListOf<MatchResult>()
        allMatchResults.add(getMatchingPrefixByConsumingSymbol(symbols, currentState))
        allMatchResults.addAll(
                getAllMatchResultsFromEmptySymbolTransition(symbols, currentState)
        )
        return allMatchResults
                .filter(MatchResult::isMatchDetected)
                .maxByOrNull { it.matchedSymbols.size } ?: result
    }

    private fun getMatchingPrefixByConsumingSymbol(symbols: List<String>, currentState: State): MatchResult {
        var nextResult = MatchResult()
        if (symbols.isEmpty()) {
            return nextResult
        }
        val currentSymbol = symbols[0]
        val nextState = currentState.getSymbolTransitions()[currentSymbol]
        if (nextState != null) {
            val nextSubsequence = symbols.subList(1, symbols.size)
            nextResult = getMatchingPrefix(nextSubsequence, nextState)
            nextResult.prependMatchingSymbol(currentSymbol)
        }
        return nextResult
    }

    private fun getAllMatchResultsFromEmptySymbolTransition(symbols: List<String>, currentState: State): List<MatchResult> {
        return currentState.getEmptySymbolTransitions()
                .map { n: State -> getMatchingPrefix(symbols, n) }
    }

    companion object {
        /**
         * Create an automaton that allows for a transition without consuming any symbol.
         *
         * @return an instance of NFA
         */
        fun newEmptySymbolNFA(): NFA {
            val start = State()
            val end = State()
            start.addEmptySymbolTransition(end)
            return NFA(start, end)
        }

        /**
         * Create an automaton that allows for a transition only after consuming the given symbol.
         *
         * @param symbol a symbol necessary to be consumed for the automaton to reach its end state.
         * @return an instance of NFA
         */
        fun newSymbolNFA(symbol: String?): NFA {
            val start = State()
            val end = State()
            start.addSymbolTransitions(symbol!!, end)
            return NFA(start, end)
        }

        /**
         * Create an automaton that reaches its final state if the first and then the second does.
         *
         * @param first the first automaton
         * @param second the second automaton
         * @return an instance of NFA
         */
        fun newConcatenateNFA(first: NFA, second: NFA): NFA {
            first.end.addEmptySymbolTransition(second.start)
            return NFA(first.start, second.end)
        }

        /**
         * Create an automaton that reaches its final state if the first or the second does.
         *
         * @param first the first automaton
         * @param second the second automaton
         * @return an instance of NFA
         */
        fun newUnionNFA(first: NFA, second: NFA): NFA {
            val start = State()
            start.addEmptySymbolTransition(first.start)
            start.addEmptySymbolTransition(second.start)
            val end = State()
            first.end.addEmptySymbolTransition(end)
            second.end.addEmptySymbolTransition(end)
            return NFA(start, end)
        }

        /**
         * Create an automaton that reaches its final state if the given automaton does it 0 or more times.
         *
         * @param automaton the given automaton
         * @return an instance of NFA
         */
        fun newKleeneClosureNFA(automaton: NFA): NFA {
            val start = State()
            val end = State()
            start.addEmptySymbolTransition(automaton.start)
            automaton.end.addEmptySymbolTransition(end)
            automaton.end.addEmptySymbolTransition(automaton.start)
            start.addEmptySymbolTransition(end)
            return NFA(start, end)
        }

        /**
         * Create an automaton that reaches its final state if the given automaton does it 0 or 1 time.
         *
         * @param automaton the given automaton
         * @return an instance of NFA
         */
        fun newZeroOrOneNFA(automaton: NFA): NFA {
            val start = State()
            val end = State()
            start.addEmptySymbolTransition(automaton.start)
            automaton.end.addEmptySymbolTransition(end)
            start.addEmptySymbolTransition(end)
            return NFA(start, end)
        }

        /**
         * Create an automaton that reaches its final state if the given automaton does it once or more.
         *
         * @param automaton the given automaton
         * @return an instance of NFA
         */
        fun newOneOrMoreNFA(automaton: NFA): NFA {
            return newConcatenateNFA(automaton, newKleeneClosureNFA(automaton))
        }
    }
}