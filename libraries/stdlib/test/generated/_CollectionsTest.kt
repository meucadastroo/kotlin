/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package test.collections

//
// NOTE: THIS FILE IS AUTO-GENERATED by the GenerateStandardLibTests.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//

import kotlin.test.*

class _CollectionsTest {
    @Test
    fun foldIndexed_Iterable() {
        expect(8) { listOf<Int>(1, 2, 3).foldIndexed(0) { i, acc, e -> acc + i.toInt() * e } }
        expect(10) { listOf<Int>(1, 2, 3).foldIndexed(1) { i, acc, e -> acc + i + e.toInt() } }
        expect(15) { listOf<Int>(1, 2, 3).foldIndexed(1) { i, acc, e -> acc * (i.toInt() + e) } }
        expect(" 0-${1} 1-${2} 2-${3}") { listOf<Int>(1, 2, 3).foldIndexed("") { i, acc, e -> "$acc $i-$e" } }
        expect(42) {
            val numbers = listOf<Int>(1, 2, 3, 4)
            numbers.foldIndexed(0) { index, a, b -> index.toInt() * (a + b) }
        }
        expect(0) {
            val numbers = listOf<Int>()
            numbers.foldIndexed(0) { index, a, b -> index.toInt() * (a + b) }
        }
        expect("${1}${1}${2}${3}${4}") {
            val numbers = listOf<Int>(1, 2, 3, 4)
            numbers.map { it.toString() }.foldIndexed("") { index, a, b -> if (index == 0) a + b + b else a + b }
        }
    }

    @Test
    fun foldRightIndexed_List() {
        expect(8) { listOf<Int>(1, 2, 3).foldRightIndexed(0) { i, e, acc -> acc + i.toInt() * e } }
        expect(10) { listOf<Int>(1, 2, 3).foldRightIndexed(1) { i, e, acc -> acc + i + e.toInt() } }
        expect(15) { listOf<Int>(1, 2, 3).foldRightIndexed(1) { i, e, acc -> acc * (i.toInt() + e) } }
        expect(" 2-${3} 1-${2} 0-${1}") { listOf<Int>(1, 2, 3).foldRightIndexed("") { i, e, acc -> "$acc $i-$e" } }
        expect("${1}${2}${3}${4}3210") {
            val numbers = listOf<Int>(1, 2, 3, 4)
            numbers.map { it.toString() }.foldRightIndexed("") { index, a, b -> a + b + index }
        }
    }

    @Test
    fun minBy_Iterable() {
        assertEquals(null, listOf<Int>().minBy { it })
        assertEquals(1, listOf<Int>(1).minBy { it })
        assertEquals(2, listOf<Int>(3, 2).minBy { it * it })
        assertEquals(3, listOf<Int>(3, 2).minBy { "a" })
        assertEquals(2, listOf<Int>(3, 2).minBy { it.toString() })
            assertEquals(3, listOf<Int>(2, 3).minBy { -it })
            
        assertEquals('b', listOf('a', 'b').maxBy { "x$it" })
        assertEquals("abc", listOf("b", "abc").maxBy { it.length })
    }

    @Test
    fun minWith_Iterable() {
        assertEquals(null, listOf<Int>().minWith(naturalOrder()))
        assertEquals(1, listOf<Int>(1).minWith(naturalOrder()))
        assertEquals(4, listOf<Int>(2, 3, 4).minWith(compareBy { it % 4 }))
    }

    @Test
    fun indexOf_Iterable() {
        expect(-1) { listOf<Int>(1, 2, 3).indexOf(0) }
        expect(0) { listOf<Int>(1, 2, 3).indexOf(1) }
        expect(1) { listOf<Int>(1, 2, 3).indexOf(2) }
        expect(2) { listOf<Int>(1, 2, 3).indexOf(3) } 
        expect(-1) { listOf("cat", "dog", "bird").indexOf("mouse") }
        expect(0) { listOf("cat", "dog", "bird").indexOf("cat") }
        expect(1) { listOf("cat", "dog", "bird").indexOf("dog") }
        expect(2) { listOf("cat", "dog", "bird").indexOf("bird") }
        expect(0) { listOf(null, "dog", null).indexOf(null as String?)}
    }

    @Test
    fun indexOf_List() {
        expect(-1) { listOf<Int>(1, 2, 3).indexOf(0) }
        expect(0) { listOf<Int>(1, 2, 3).indexOf(1) }
        expect(1) { listOf<Int>(1, 2, 3).indexOf(2) }
        expect(2) { listOf<Int>(1, 2, 3).indexOf(3) } 
        expect(-1) { listOf("cat", "dog", "bird").indexOf("mouse") }
        expect(0) { listOf("cat", "dog", "bird").indexOf("cat") }
        expect(1) { listOf("cat", "dog", "bird").indexOf("dog") }
        expect(2) { listOf("cat", "dog", "bird").indexOf("bird") }
        expect(0) { listOf(null, "dog", null).indexOf(null as String?)}
    }

    @Test
    fun indexOfFirst_Iterable() {
        expect(-1) { listOf<Int>(1, 2, 3).indexOfFirst { it == 0 } }
        expect(0) { listOf<Int>(1, 2, 3).indexOfFirst { it % 2 == 1 } }
        expect(1) { listOf<Int>(1, 2, 3).indexOfFirst { it % 2 == 0 } }
        expect(2) { listOf<Int>(1, 2, 3).indexOfFirst { it == 3 } }
        expect(-1) { listOf("cat", "dog", "bird").indexOfFirst { it.contains("p") } }
        expect(0) { listOf("cat", "dog", "bird").indexOfFirst { it.startsWith('c') } }
        expect(1) { listOf("cat", "dog", "bird").indexOfFirst { it.startsWith('d') } }
        expect(2) { listOf("cat", "dog", "bird").indexOfFirst { it.endsWith('d') } }
    }

    @Test
    fun indexOfFirst_List() {
        expect(-1) { listOf<Int>(1, 2, 3).indexOfFirst { it == 0 } }
        expect(0) { listOf<Int>(1, 2, 3).indexOfFirst { it % 2 == 1 } }
        expect(1) { listOf<Int>(1, 2, 3).indexOfFirst { it % 2 == 0 } }
        expect(2) { listOf<Int>(1, 2, 3).indexOfFirst { it == 3 } }
        expect(-1) { listOf("cat", "dog", "bird").indexOfFirst { it.contains("p") } }
        expect(0) { listOf("cat", "dog", "bird").indexOfFirst { it.startsWith('c') } }
        expect(1) { listOf("cat", "dog", "bird").indexOfFirst { it.startsWith('d') } }
        expect(2) { listOf("cat", "dog", "bird").indexOfFirst { it.endsWith('d') } }
    }

}
