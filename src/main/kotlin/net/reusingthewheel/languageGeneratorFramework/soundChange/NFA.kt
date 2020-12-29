package net.reusingthewheel.languageGeneratorFramework.soundChange


/**
 * A nondeterministic finite state automaton
 */
class NFA private constructor(private val start: State, private val end: EpsilonTransitionState) {

    /**
     * Find all subsequences matching this NFA in given sequence of symbols and capture starting
     * and ending indexes of capturing groups specified in NFA
     *
     * @param symbols a list of symbols.
     * @return MatchResult with information on whether the final state has been reached, the subsequence
     * of symbols consumed in the process and a list of captured indexes
     */
    fun findAllMatchingSubsequencesAndCapturedIndexes(symbols: List<String>): MatchResult {
        require(symbols.isNotEmpty()) { "A sequence of symbols cannot be empty" }
        var lastIndex = 0
        val allMatchResults = mutableListOf<MatchResult>()
        while (lastIndex < symbols.size) {
            val matchResult = start.getFirstMatch(symbols, lastIndex)
            if (matchResult.isMatchDetected) {
                allMatchResults.add(matchResult)
                lastIndex = matchResult.capturedIndexes.getOrElse(0) { lastIndex + 1 }
            } else {
                lastIndex += 1;
            }
        }
        val finalResult = MatchResult()
        if (allMatchResults.isNotEmpty()) {
            finalResult.isMatchDetected = true
            finalResult.capturedIndexes.addAll(allMatchResults.map { it.capturedIndexes }.flatten())
            finalResult.matchedSymbols.addAll(allMatchResults.map { it.matchedSymbols }.flatten())
        }
        return finalResult
    }

    /**
     * Generate all sequences of symbols such as this NFA reaches its final state after consuming all symbols
     * in the sequence
     * @return list of the matching sequences as lists of symbols
     */
    fun generateAllMatchingSymbolSequences(): List<List<String>> {
        return start.generateAllMatchingSymbolSequences()
    }

    companion object {
        /**
         * Create an automaton that allows for a transition without consuming any symbol.
         *
         * @return an instance of NFA
         */
        fun newEmptySymbolNFA(): NFA {
            val start = EpsilonTransitionState()
            val end = EpsilonTransitionState()
            start.addTransition(end)
            return NFA(start, end)
        }

        /**
         * Create an automaton that allows for a transition only after consuming the given symbol.
         *
         * @param symbol a symbol necessary to be consumed for the automaton to reach its end state.
         * @return an instance of NFA
         */
        fun newSymbolNFA(symbol: String): NFA {
            val end = EpsilonTransitionState()
            val start = SymbolConsumingState(symbol, end)
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
            first.end.addTransition(second.start)
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
            val start = EpsilonTransitionState()
            start.addTransition(first.start)
            start.addTransition(second.start)
            val end = EpsilonTransitionState()
            first.end.addTransition(end)
            second.end.addTransition(end)
            return NFA(start, end)
        }

        /**
         * Create an automaton that reaches its final state if the given automaton does it 0 or 1 time.
         *
         * @param automaton the given automaton
         * @param isLazy if true, the resulting automaton matches as short a sequence of symbols as possible to
         * detect a match.
         * @return an instance of NFA
         */
        fun newZeroOrOneNFA(automaton: NFA, isLazy: Boolean = false): NFA {
            val start = EpsilonTransitionState()
            val end = EpsilonTransitionState()
            val fromStart = reverseIfTrue(listOf(automaton.start, end), isLazy)
            start.addTransitions(fromStart)
            automaton.end.addTransition(end)
            return NFA(start, end)
        }

        /**
         * Create an automaton that captures indexes before and after given automaton.
         *
         * @param automaton the given automaton
         * @return an instance of NFA
         */
        fun newIndexCapturingGroup(automaton: NFA): NFA {
            val start = EpsilonTransitionState(true)
            val end = EpsilonTransitionState(true)
            start.addTransitions(listOf(automaton.start))
            automaton.end.addTransition(end)
            return NFA(start, end)
        }

        private fun reverseIfTrue(states: List<State>, shouldBeReversed: Boolean): List<State> {
            if(shouldBeReversed) {
                return states.reversed()
            }
            return states
        }
    }
}