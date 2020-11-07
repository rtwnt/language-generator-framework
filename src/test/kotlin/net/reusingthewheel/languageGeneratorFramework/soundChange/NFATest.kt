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

object NFATest: Spek({
    describe(" A finite state automaton") {
        val setup = mapOf(
                NFA.newEmptySymbolNFA() to "emptySymbolNfa" to mapOf(
                        listOf("a") to expectedMatchResult(true),
                        listOf("a", "x") to expectedMatchResult(true),
                        listOf("x", "a") to expectedMatchResult(true),
                ),
                NFA.newSymbolNFA("a") to "symbolNFA" to mapOf(
                        listOf("a") to expectedMatchResult(true, listOf("a")),
                        listOf("b") to expectedMatchResult(false),
                        listOf("a", "b") to expectedMatchResult(true, listOf("a")),
                        listOf("b", "a") to expectedMatchResult(false),
                ),
                NFA.newConcatenateNFA(NFA.newSymbolNFA("a"), NFA.newSymbolNFA("b")) to "concatenate" to mapOf(
                        listOf("a") to expectedMatchResult(false),
                        listOf("b") to expectedMatchResult(false),
                        listOf("a", "b") to expectedMatchResult(true, listOf("a", "b")),
                        listOf("b", "a") to expectedMatchResult(false),
                        listOf("a", "b", "x") to expectedMatchResult(true, listOf("a", "b")),
                        listOf("x", "a", "b", "x") to expectedMatchResult(false),
                ),
                NFA.newUnionNFA(NFA.newSymbolNFA("a"), NFA.newSymbolNFA("b")) to "union" to mapOf(
                        listOf("a") to expectedMatchResult(true, listOf("a")),
                        listOf("a", "x") to expectedMatchResult(true, listOf("a")),
                        listOf("a", "b") to expectedMatchResult(true, listOf("a")),
                        listOf("b", "a") to expectedMatchResult(true, listOf("b")),
                        listOf("b") to expectedMatchResult(true, listOf("b")),
                        listOf("b", "x") to expectedMatchResult(true, listOf("b")),
                        listOf("x", "a") to expectedMatchResult(false),
                        listOf("x", "b") to expectedMatchResult(false),
                ),
                NFA.newKleeneClosureNFA(NFA.newSymbolNFA("a")) to "kleeneClosure" to mapOf(
                        listOf("a") to expectedMatchResult(true, listOf("a")),
                        listOf("a", "x") to expectedMatchResult(true, listOf("a")),
                        listOf("a", "a", "x") to expectedMatchResult(true, listOf("a", "a")),
                        listOf("x", "a", "x") to expectedMatchResult(true),
                        listOf("x", "a", "a") to expectedMatchResult(true),
                        listOf("x", "b") to expectedMatchResult(true),
                ),
                NFA.newZeroOrOneNFA(NFA.newSymbolNFA("a")) to "zeroOrOne" to mapOf(
                        listOf("a") to expectedMatchResult(true, listOf("a")),
                        listOf("a", "x") to expectedMatchResult(true, listOf("a")),
                        listOf("a", "a", "x") to expectedMatchResult(true, listOf("a")),
                        listOf("x", "a", "x") to expectedMatchResult(true),
                        listOf("x", "b", "x") to expectedMatchResult(true),
                ),
                NFA.newOneOrMoreNFA(NFA.newSymbolNFA("a")) to "oneOrMore" to mapOf(
                        listOf("a") to expectedMatchResult(true, listOf("a")),
                        listOf("a", "x") to expectedMatchResult(true, listOf("a")),
                        listOf("a", "a", "x") to expectedMatchResult(true, listOf("a", "a")),
                        listOf("x", "a", "x") to expectedMatchResult(false),
                        listOf("x", "a", "a", "x") to expectedMatchResult(false),
                        listOf("x", "b", "x") to expectedMatchResult(false),
                ),
        )

        describe("getMatchingPrefix method") {
            setup.forEach { (key, value) ->
                describe("called on ${key.second}") {
                    it("throws IllegalArgumentException when given an empty sequence of symbols") {
                        Assertions.assertThatThrownBy { key.first.getMatchingPrefix(listOf()) }.isInstanceOf(IllegalArgumentException::class.java)
                    }

                    value.forEach { (symbols, expected) ->
                        it("returns MatchResult with isMatchDetected = " +
                                "${expected.isMatchDetected} and matchedSymbols = ${expected.matchedSymbols} when given " +
                                "a sequence of symbols = $symbols") {
                            Assertions.assertThat(key.first.getMatchingPrefix(symbols)).usingRecursiveComparison().isEqualTo(expected)
                        }
                    }
                }
            }
        }
    }

})