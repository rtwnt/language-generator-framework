package net.reusingthewheel.languageGeneratorFramework.soundChange

import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.lang.IllegalArgumentException


fun expectedMatchResult(matchDetected: Boolean, matchedSymbols: List<String> = listOf()): MatchResult {
    val result = MatchResult()
    result.isMatchDetected = matchDetected
    result.matchedSymbols.addAll(matchedSymbols)
    return result
}

class NFAInstanceTestSetup(private val toTest: () -> NFA, val name: String, val inputToExpectedMatchResult: Map<List<String>, MatchResult>) {
    fun assertThrowsIllegalArgumentException() {
        Assertions.assertThatThrownBy { toTest().getMatchingPrefix(listOf()) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    fun assertGetMatchingPrefixReturnsExpectedResult(symbols: List<String>, expected: MatchResult) {
        Assertions.assertThat(toTest().getMatchingPrefix(symbols)).usingRecursiveComparison().isEqualTo(expected)
    }
}

object NFATest: Spek({
    describe(" A finite state automaton") {
        val setup = listOf(
                NFAInstanceTestSetup(
                        { NFA.newEmptySymbolNFA() },
                        "emptySymbolNfa",
                        mapOf(
                                listOf("a") to expectedMatchResult(true),
                                listOf("a", "x") to expectedMatchResult(true),
                                listOf("x", "a") to expectedMatchResult(true),
                        )
                ),
                NFAInstanceTestSetup(
                        { NFA.newSymbolNFA("a") },
                        "symbolNFA",
                        mapOf(
                                listOf("a") to expectedMatchResult(true, listOf("a")),
                                listOf("b") to expectedMatchResult(false),
                                listOf("a", "b") to expectedMatchResult(true, listOf("a")),
                                listOf("b", "a") to expectedMatchResult(false),
                        )
                ),
                NFAInstanceTestSetup(
                        { NFA.newConcatenateNFA(NFA.newSymbolNFA("a"), NFA.newSymbolNFA("b")) },
                        "concatenate",
                        mapOf(
                                listOf("a") to expectedMatchResult(false),
                                listOf("b") to expectedMatchResult(false),
                                listOf("a", "b") to expectedMatchResult(true, listOf("a", "b")),
                                listOf("b", "a") to expectedMatchResult(false),
                                listOf("a", "b", "x") to expectedMatchResult(true, listOf("a", "b")),
                                listOf("x", "a", "b", "x") to expectedMatchResult(false),
                        )
                ),
                NFAInstanceTestSetup(
                        { NFA.newUnionNFA(NFA.newSymbolNFA("a"), NFA.newSymbolNFA("b")) },
                        "union",
                        mapOf(
                                listOf("a") to expectedMatchResult(true, listOf("a")),
                                listOf("a", "x") to expectedMatchResult(true, listOf("a")),
                                listOf("a", "b") to expectedMatchResult(true, listOf("a")),
                                listOf("b", "a") to expectedMatchResult(true, listOf("b")),
                                listOf("b") to expectedMatchResult(true, listOf("b")),
                                listOf("b", "x") to expectedMatchResult(true, listOf("b")),
                                listOf("x", "a") to expectedMatchResult(false),
                                listOf("x", "b") to expectedMatchResult(false),
                        )
                ),
                NFAInstanceTestSetup(
                        { NFA.newKleeneClosureNFA(NFA.newSymbolNFA("a")) },
                        "kleeneClosure",
                        mapOf(
                                listOf("a") to expectedMatchResult(true, listOf("a")),
                                listOf("a", "x") to expectedMatchResult(true, listOf("a")),
                                listOf("a", "a", "x") to expectedMatchResult(true, listOf("a", "a")),
                                listOf("x", "a", "x") to expectedMatchResult(true),
                                listOf("x", "a", "a") to expectedMatchResult(true),
                                listOf("x", "b") to expectedMatchResult(true),
                        )
                ),
                NFAInstanceTestSetup(
                        { NFA.newConcatenateNFA(
                                NFA.newKleeneClosureNFA(NFA.newSymbolNFA("a"), true),
                                NFA.newSymbolNFA("x")
                        ) },
                        "lazyKleeneClosureWithConcatenation",
                        mapOf(
                                listOf("a") to expectedMatchResult(false),
                                listOf("a", "x") to expectedMatchResult(true, listOf("a", "x")),
                                listOf("a", "a", "x") to expectedMatchResult(true, listOf("a", "a", "x")),
                                listOf("x", "a", "x") to expectedMatchResult(true, listOf("x")),
                                listOf("x", "a", "a") to expectedMatchResult(true, listOf("x")),
                                listOf("x", "b") to expectedMatchResult(true, listOf("x")),
                        )
                ),
                NFAInstanceTestSetup(
                        { NFA.newZeroOrOneNFA(NFA.newSymbolNFA("a")) },
                        "zeroOrOne",
                        mapOf(
                                listOf("a") to expectedMatchResult(true, listOf("a")),
                                listOf("a", "x") to expectedMatchResult(true, listOf("a")),
                                listOf("a", "a", "x") to expectedMatchResult(true, listOf("a")),
                                listOf("x", "a", "x") to expectedMatchResult(true),
                                listOf("x", "b", "x") to expectedMatchResult(true),
                        )
                ),
                NFAInstanceTestSetup(
                        { NFA.newConcatenateNFA(
                                NFA.newZeroOrOneNFA(NFA.newSymbolNFA("a"), true),
                                NFA.newSymbolNFA("x")
                        ) },
                        "lazyZeroOrOneWithConcatenate",
                        mapOf(
                                listOf("a") to expectedMatchResult(false),
                                listOf("a", "x") to expectedMatchResult(true, listOf("a", "x")),
                                listOf("a", "a", "x") to expectedMatchResult(false),
                                listOf("x", "a", "x") to expectedMatchResult(true, listOf("x")),
                                listOf("x", "b", "x") to expectedMatchResult(true, listOf("x")),
                        )
                ),
                NFAInstanceTestSetup(
                        { NFA.newOneOrMoreNFA(NFA.newSymbolNFA("a")) },
                        "oneOrMore",
                        mapOf(
                                listOf("a") to expectedMatchResult(true, listOf("a")),
                                listOf("a", "x") to expectedMatchResult(true, listOf("a")),
                                listOf("a", "a", "x") to expectedMatchResult(true, listOf("a", "a")),
                                listOf("x", "a", "x") to expectedMatchResult(false),
                                listOf("x", "a", "a", "x") to expectedMatchResult(false),
                                listOf("x", "b", "x") to expectedMatchResult(false),
                        )
                ),
                NFAInstanceTestSetup(
                        { NFA.newConcatenateNFA(
                                NFA.newOneOrMoreNFA(NFA.newSymbolNFA("a"), true),
                                NFA.newSymbolNFA("x")
                        ) },
                        "lazyOneOrMoreWithConcatenate",
                        mapOf(
                                listOf("a") to expectedMatchResult(false),
                                listOf("a", "x") to expectedMatchResult(true, listOf("a", "x")),
                                listOf("a", "a", "x") to expectedMatchResult(true, listOf("a", "a", "x")),
                                listOf("x", "a", "x") to expectedMatchResult(false),
                                listOf("x", "a", "a", "x") to expectedMatchResult(false),
                                listOf("x", "b", "x") to expectedMatchResult(false),
                        )
                )
        )

        describe("getMatchingPrefix method") {
            setup.forEach { nfaSetup ->
                describe("called on ${nfaSetup.name}") {
                    it("throws IllegalArgumentException when given an empty sequence of symbols") {
                        nfaSetup.assertThrowsIllegalArgumentException()
                    }

                    nfaSetup.inputToExpectedMatchResult.forEach { (symbols, expected) ->
                        it("returns MatchResult with isMatchDetected = " +
                                "${expected.isMatchDetected} and matchedSymbols = ${expected.matchedSymbols} when given " +
                                "a sequence of symbols = $symbols") {
                            nfaSetup.assertGetMatchingPrefixReturnsExpectedResult(symbols, expected)
                        }
                    }
                }
            }
        }
    }

})