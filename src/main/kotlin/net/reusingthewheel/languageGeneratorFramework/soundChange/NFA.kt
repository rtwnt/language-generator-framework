package net.reusingthewheel.languageGeneratorFramework.soundChange


/**
 * A nondeterministic finite state automaton
 */
class NFA private constructor(private val start: State, private val end: EpsilonTransitionState) {

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
        return start.getFirstMatchingPrefix(symbols)
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
         * Create an automaton that reaches its final state if the given automaton does it 0 or more times.
         *
         * @param automaton the given automaton
         * @param isLazy if true, the resulting automaton matches as short a sequence of symbols as possible to
         * detect a match.
         * @return an instance of NFA
         */
        fun newKleeneClosureNFA(automaton: NFA, isLazy: Boolean = false): NFA {
            val start = EpsilonTransitionState()
            val end = EpsilonTransitionState()
            val fromStart = reverseIfTrue(listOf(automaton.start, end), isLazy)
            start.addTransitions(fromStart)
            automaton.end.addTransition(automaton.start)
            automaton.end.addTransition(end)
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

        fun newLazy(automaton: NFA): NFA {
            val start = EpsilonTransitionState()
            val end = EpsilonTransitionState()
            start.addTransitions(listOf(end, automaton.start))
            automaton.end.addTransition(end)
            return NFA(start, end)
        }

        /**
         * Create an automaton that reaches its final state if the given automaton does it once or more.
         *
         * @param automaton the given automaton
         * @param isLazy if true, the resulting automaton matches as short a sequence of symbols as possible to
         * detect a match.
         * @return an instance of NFA
         */
        fun newOneOrMoreNFA(automaton: NFA, isLazy: Boolean = false): NFA {
            return newConcatenateNFA(automaton, newKleeneClosureNFA(automaton, isLazy))
        }

        private fun reverseIfTrue(states: List<State>, shouldBeReversed: Boolean): List<State> {
            if(shouldBeReversed) {
                return states.reversed()
            }
            return states
        }
    }
}