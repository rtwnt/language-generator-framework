package net.reusingthewheel.languageGeneratorFramework.soundChange

import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.lang.IllegalArgumentException


fun expectedMatchResult(matchDetected: Boolean, matchedSymbols: List<String> = listOf(), capturedIndexes: List<Int> = listOf()): MatchResult {
    val result = MatchResult()
    result.isMatchDetected = matchDetected
    result.matchedSymbols.addAll(matchedSymbols)
    result.capturedIndexes.addAll(capturedIndexes)
    return result
}

class NFAInstanceTestSetup(
        private val toTest: () -> NFA,
        val name: String,
        val inputToExpectedMatchResult: Map<List<String>, MatchResult>,
        private val expectedResultOfGenerateAllMatchingSequences: List<List<String>>
) {
    fun assertThrowsIllegalArgumentException() {
        Assertions.assertThatThrownBy { toTest().getMatchingPrefix(listOf()) }.isInstanceOf(IllegalArgumentException::class.java)
    }

    fun assertGetMatchingPrefixReturnsExpectedResult(symbols: List<String>, expected: MatchResult) {
        Assertions.assertThat(toTest().getMatchingPrefix(symbols)).usingRecursiveComparison().isEqualTo(expected)
    }

    fun assertGenerateAllMatchingSequencesReturnsExpectedResults() {
        Assertions.assertThat(toTest().generateAllMatchingSymbolSequences())
                .containsExactlyInAnyOrderElementsOf(expectedResultOfGenerateAllMatchingSequences)
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
                        ),
                        listOf(listOf())
                ),
                NFAInstanceTestSetup(
                        { NFA.newSymbolNFA("a") },
                        "symbolNFA",
                        mapOf(
                                listOf("a") to expectedMatchResult(true, listOf("a")),
                                listOf("b") to expectedMatchResult(false),
                                listOf("a", "b") to expectedMatchResult(true, listOf("a")),
                                listOf("b", "a") to expectedMatchResult(false),
                        ),
                        listOf(listOf("a"))
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
                        ),
                        listOf(listOf("a", "b"))
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
                        ),
                        listOf(listOf("a"), listOf("b"))
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
                        ),
                        listOf(listOf(), listOf("a"))
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
                        ),
                        listOf(listOf("x"), listOf("a", "x"))
                ),
                NFAInstanceTestSetup(
                        {
                            val captured = NFA.newConcatenateNFA(NFA.newSymbolNFA("b"), NFA.newSymbolNFA("c"))
                            val capturingGroup = NFA.newIndexCapturingGroup(captured)
                            val concatenateLeft = NFA.newConcatenateNFA(NFA.newSymbolNFA("a"), capturingGroup)
                            NFA.newConcatenateNFA(concatenateLeft, NFA.newSymbolNFA("d"))
                        },
                        "concatenateWithCapturingIndexes",
                        mapOf(
                                listOf("a", "b", "c", "d") to expectedMatchResult(true, listOf("a", "b", "c", "d"), listOf(3, 1)),
                        ),
                        listOf(listOf("a", "b", "c", "d"))
                )
        )

        setup.forEach { nfaSetup ->
            describe("For ${nfaSetup.name}") {
                describe("getMatchingPregix method") {
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

                describe("generateAllMatchingSymbolSequences method") {
                    it("returns all matching symbol sequences") {
                        nfaSetup.assertGenerateAllMatchingSequencesReturnsExpectedResults()
                    }
                }
            }
        }
    }

})