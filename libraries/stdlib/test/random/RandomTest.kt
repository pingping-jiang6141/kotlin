/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package test.random

import kotlin.math.*
import kotlin.random.*
import kotlin.test.*

abstract class RandomSmokeTest {
    abstract val subject: Random

    @Test
    fun nextBits() {
        repeat(100) {
            assertEquals(0, subject.nextBits(0))
        }

        for (bitCount in 1..32) {
            val upperBitCount = 32 - bitCount
            var result1 = 0
            var result2 = -1
            repeat(1000) {
                val bits = subject.nextBits(bitCount)
                result1 = result1 or bits
                result2 = result2 and bits

                assertEquals(0, bits.ushr(bitCount - 1).ushr(1), "Upper $upperBitCount bits should be zero")
            }

            assertEquals(1.shl(bitCount - 1).shl(1) - 1, result1, "Lower $bitCount bits should be filled")
            assertEquals(0, result2, "All zero bits should present")
        }
    }


    @Test
    fun nextInt() {
        var result1 = 0
        var result2 = -1
        repeat(1000) {
            val r = subject.nextInt()
            result1 = result1 or r
            result2 = result2 and r
        }
        assertEquals(-1, result1, "All one bits should present")
        assertEquals(0, result2, "All zero bits should present")
    }

    @Test
    fun nextIntBound() {
        assertFailsWith<IllegalArgumentException> { subject.nextInt(0) }
        assertFailsWith<IllegalArgumentException> { subject.nextInt(-1) }
        assertFailsWith<IllegalArgumentException> { subject.nextInt(Int.MIN_VALUE) }

        repeat(1000) {
            assertEquals(0, subject.nextInt(1))
        }

        for (bound in listOf(2, 3, 7, 16, 32, 0x4000_0000, Int.MAX_VALUE)) {
            repeat(1000) {
                val x = subject.nextInt(bound)
                if (x !in 0 until bound)
                    fail("Value $x must be in range [0, $bound)")
            }
        }
    }

    @Test
    fun nextIntOriginBound() {
        assertFailsWith<IllegalArgumentException> { subject.nextInt(0, 0) }
        assertFailsWith<IllegalArgumentException> { subject.nextInt(-1, -2) }
        assertFailsWith<IllegalArgumentException> { subject.nextInt(Int.MIN_VALUE, Int.MIN_VALUE) }

        for (n in Int.MIN_VALUE until Int.MAX_VALUE step 0x10000) {
            assertEquals(n, subject.nextInt(n, n + 1))
        }
        (Int.MAX_VALUE - 1).let { n ->
            assertEquals(n, subject.nextInt(n, n + 1))
        }

        for ((origin, bound) in listOf((0 to 2), (-1 to 5), (0 to 32), (0 to Int.MAX_VALUE), (-1 to Int.MAX_VALUE), (Int.MIN_VALUE to Int.MAX_VALUE))) {
            repeat(1000) {
                val x = subject.nextInt(origin, bound)
                if (x !in origin until bound)
                    fail("Value $x must be in range [$origin, $bound)")
            }
        }
    }

    @Suppress("EmptyRange")
    @Test
    fun nextIntInIntRange() {
        assertFailsWith<IllegalArgumentException> { subject.nextInt(0 until 0) }
        assertFailsWith<IllegalArgumentException> { subject.nextInt(-1..Int.MIN_VALUE) }
        assertFailsWith<IllegalArgumentException> { subject.nextInt(Int.MAX_VALUE until Int.MAX_VALUE) }

        repeat(1000) { n ->
            assertEquals(n, subject.nextInt(n..n))
        }

        for (range in listOf((0 until 2), (-1 until 5), (0 until 32), (0 until Int.MAX_VALUE),
                             (0..Int.MAX_VALUE), (Int.MIN_VALUE..0), (Int.MIN_VALUE..Int.MAX_VALUE))) {
            repeat(1000) {
                val x = subject.nextInt(range)
                if (x !in range)
                    fail("Value $x must be in range $range")
            }
        }
    }

    @Test
    fun nextLong() {
        var result1 = 0L
        var result2 = -1L
        repeat(1000) {
            val r = subject.nextLong()
            result1 = result1 or r
            result2 = result2 and r
        }
        assertEquals(-1, result1, "All one bits should present")
        assertEquals(0, result2, "All zero bits should present")
    }

    @Test
    fun nextLongBound() {
        assertFailsWith<IllegalArgumentException> { subject.nextLong(0) }
        assertFailsWith<IllegalArgumentException> { subject.nextLong(-1) }
        assertFailsWith<IllegalArgumentException> { subject.nextLong(Long.MIN_VALUE) }

        repeat(1000) {
            assertEquals(0L, subject.nextLong(1))
        }

        for (bound in listOf(2, 23, 32, 0x1_0000_0000, 0x4000_0000_0000_0000, Long.MAX_VALUE)) {
            repeat(1000) {
                val x = subject.nextLong(bound)
                if (x !in 0L until bound)
                    fail("Value $x must be in range [0, $bound)")
            }
        }
    }

    @Test
    fun nextLongOriginBound() {
        assertFailsWith<IllegalArgumentException> { subject.nextLong(0, 0) }
        assertFailsWith<IllegalArgumentException> { subject.nextLong(-1, -2) }
        assertFailsWith<IllegalArgumentException> { subject.nextLong(Long.MIN_VALUE, Long.MIN_VALUE) }

        for (i in -500..500) {
            val n = 0x1_0000_0000 + i
            assertEquals(n, subject.nextLong(n, n + 1))
        }

        for ((origin, bound) in listOf((0L to 32L), (-1L to 5L), (0L to 0x1_0000_0000),
                                       (0L to Long.MAX_VALUE), (-1L to Long.MAX_VALUE), (Long.MIN_VALUE to Long.MAX_VALUE))) {
            repeat(1000) {
                val x = subject.nextLong(origin, bound)
                if (x !in origin until bound)
                    fail("Value $x must be in range [$origin, $bound)")
            }
        }
    }

    @Suppress("EmptyRange")
    @Test
    fun nextLongInLongRange() {
        assertFailsWith<IllegalArgumentException> { subject.nextLong(0L until 0L) }
        assertFailsWith<IllegalArgumentException> { subject.nextLong(-1..Long.MIN_VALUE) }
        assertFailsWith<IllegalArgumentException> { subject.nextLong(Long.MAX_VALUE until Long.MAX_VALUE) }

        repeat(1000) { i ->
            val n = 0x1_0000_0000 - 500 + i
            assertEquals(n, subject.nextLong(n..n))
        }

        for (range in listOf((0L until 2L), (-1L until 5L), (0L until 32L), (0L until Long.MAX_VALUE),
                             (0L..Long.MAX_VALUE), (Long.MIN_VALUE..0L), (Long.MIN_VALUE..Long.MAX_VALUE))) {
            repeat(1000) {
                val x = subject.nextLong(range)
                if (x !in range)
                    fail("Value $x must be in range $range")
            }
        }
    }


    @Test
    fun nextDouble() {
        repeat(10000) {
            val d = subject.nextDouble()
            if (!(d >= 0.0 && d < 1)) {
                fail("Random double $d is out of range")
            }
        }
    }

    @Test
    fun nextDoubleBound() {
        assertFailsWith<IllegalArgumentException> { subject.nextDouble(-1.0) }
        assertFailsWith<IllegalArgumentException> { subject.nextDouble(-0.0) }
        assertFailsWith<IllegalArgumentException> { subject.nextDouble(0.0) }
        assertFailsWith<IllegalArgumentException> { subject.nextDouble(Double.NaN) }

        repeat(100) {
            assertEquals(0.0, subject.nextDouble(0.0.nextUp()))
        }

        assertTrue(subject.nextDouble(Double.POSITIVE_INFINITY).isFinite(), "Infinity is exclusive")

        for (bound in listOf(1.0, 100.0, 1024.0, Double.MAX_VALUE)) {
            repeat(1000) {
                val d = subject.nextDouble(bound)
                if (!(d >= 0.0 && d < bound)) {
                    fail("Random double $d is out of range [0, $bound)")
                }
            }
        }
    }

    @Test
    fun nextDoubleOriginBound() {
        assertFailsWith<IllegalArgumentException> { subject.nextDouble(0.0, -1.0) }
        assertFailsWith<IllegalArgumentException> { subject.nextDouble(0.0, Double.NaN) }
        assertFailsWith<IllegalArgumentException> { subject.nextDouble(Double.NaN, 0.0) }
        assertFailsWith<IllegalArgumentException> { subject.nextDouble(Double.NaN, Double.POSITIVE_INFINITY) }
        assertFailsWith<IllegalArgumentException> { subject.nextDouble(Double.MAX_VALUE, Double.MAX_VALUE) }

        for (exp in -1022..1023) {
            val origin = 2.0.pow(exp)
            assertEquals(origin, subject.nextDouble(origin, origin.nextUp()), "2^$exp")
            assertEquals(-origin, subject.nextDouble(-origin, (-origin).nextUp()), "-(2^$exp)")
        }

        run {
            val size = 1000
            val fullRangeValues = (1..size).map { subject.nextDouble(-Double.MAX_VALUE, Double.MAX_VALUE) }.distinct()
            fullRangeValues.forEach {
                assertTrue(it.isFinite() && it < Double.MAX_VALUE)
            }
            assertTrue(fullRangeValues.size >= (size * 0.995), "All values should be distinct, but only ${fullRangeValues.size} of them are")
        }

        for ((origin, bound) in listOf(0.0 to 1.0, -1.0 to 1.0, 0.0 to 100.0, -PI to PI, 0.0 to Double.MAX_VALUE)) {
            repeat(1000) {
                val d = subject.nextDouble(origin, bound)
                if (!(d >= origin && d < bound)) {
                    fail("Random double $d is out of range [$origin, $bound)")
                }
            }
        }
    }

    @Test
    fun nextFloat() {
        repeat(10000) {
            val d = subject.nextFloat()
            if (!(d >= 0.0F && d < 1.0F)) {
                fail("Random float $d is out of range")
            }
        }
    }

    @Test
    fun nextBoolean() {
        val size = 1000
        val booleans = (1..size).map { subject.nextBoolean() }.groupingBy { it }.eachCount()
        val ts = booleans[true]!!
        val fs = booleans[false]!!

        assertNotEquals(0, ts)
        assertNotEquals(0, fs)

        val skew = abs(ts.toDouble() - fs.toDouble()) / size
        assertTrue(skew < 0.10, "Boolean generator is skewed: $booleans, delta is $skew")
    }

    @Test
    fun nextBytes() {
        val size = 20
        val bytes1 = subject.nextBytes(size)
        assertEquals(size, bytes1.size)
        assertTrue(bytes1.any { it != 0.toByte() })

        val bytes2 = subject.nextBytes(ByteArray(size))
        assertEquals(size, bytes2.size)
        assertTrue(bytes2.any { it != 0.toByte() })

        assertFalse(bytes1 contentEquals bytes2)
    }

    @Test
    fun nextBytesRange() {
        val size = 100
        val array = subject.nextBytes(size)

        assertFailsWith<IllegalArgumentException> { subject.nextBytes(array, -1, 10) }
        assertFailsWith<IllegalArgumentException> { subject.nextBytes(array, 0, size + 10) }
        assertFailsWith<IllegalArgumentException> { subject.nextBytes(array, 10, 0) }

        repeat(10000) {
            val from = subject.nextInt(0, size - 1)
            val to = subject.nextInt(from + 1, size)

            val prev = array.copyOf()
            subject.nextBytes(array, from, to)

            var noChanges = array contentEquals prev
            val rangeSize = to - from
            val retries = 4 / rangeSize
            var n = 0
            while (noChanges && n < retries) {
                // there's a small chance that a small range will get the same value as before
                // run randomization again
                subject.nextBytes(array, from, to)
                noChanges = array contentEquals prev
                n++
            }

            if (noChanges) {
                fail("Something should have changed in array after subrange [$from, $to) randomization (${1 + retries} times): " +
                     array.copyOfRange(from, to).contentToString())
            }

            for (p in 0 until from) {
                assertEquals(prev[p], array[p])
            }
            for (p in to until size) {
                assertEquals(prev[p], array[p])
            }
        }
    }
}


class DefaultRandomSmokeTest : RandomSmokeTest() {
    override val subject: Random get() = Random
}

class SeededRandomSmokeTest : RandomSmokeTest() {
    override val subject: Random get() = staticSubject

    companion object {
        val staticSubject = Random(Random.nextInt().also { println("Seed: $it") })
    }

    @Test
    fun sameIntSeed() {
        val v = subject.nextInt(1..Int.MAX_VALUE)
        for (seed in listOf(v, -v)) {
            testSameSeededRandoms(Random(seed), Random(seed), seed)
        }
    }

    @Test
    fun sameLongSeed() {
        val v = subject.nextLong(1..Long.MAX_VALUE)
        for (seed in listOf(v, -v)) {
            testSameSeededRandoms(Random(seed), Random(seed), seed)
        }
    }

    @Test
    fun sameIntLongSeed() {
        val v = subject.nextInt(1..Int.MAX_VALUE)
        for (seed in listOf(v, 0, -v)) {
            testSameSeededRandoms(Random(seed), Random(seed.toLong()), seed)
        }
    }

    private fun testSameSeededRandoms(r1: Random, r2: Random, seed: Any) {
        val seq1 = List(10) { r1.nextInt() }
        val seq2 = List(10) { r2.nextInt() }
//        println("$seed: $seq1")

        assertEquals(seq1, seq2, "Generators seeded with $seed should produce the same output")
    }
}