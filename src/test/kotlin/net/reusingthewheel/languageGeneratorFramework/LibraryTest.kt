/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package net.reusingthewheel.languageGeneratorFramework

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import org.assertj.core.api.Assertions.*

object LibrarySpec: Spek({
    describe("A library") {
        val classUnderTest by memoized { Library() }

        describe("someLibraryMethod") {
            it("returns 'true'") {
                assertThat(classUnderTest.someLibraryMethod()).isEqualTo(true)
            }
        }
    }
})
