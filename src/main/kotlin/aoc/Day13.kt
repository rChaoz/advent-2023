package aoc

import Day
import split
import java.io.PrintStream

class Day13 : Day() {
    private fun parse(lines: List<String>) = lines.split(String::isEmpty)

    private fun List<String>.rotate() = List(this[0].length) { col ->
        buildString {
            this@rotate.forEach { line -> append(line[col]) }
        }
    }

    private fun findReflection(pattern: List<String>, exclude: Int = -1): Int? {
        fun hasReflectionAt(index: Int): Boolean {
            var i = index - 1
            var j = index
            while (i >= 0 && j < pattern.size) if (pattern[i--] != pattern[j++]) return false
            return true
        }

        return (1..pattern.lastIndex).find { it != exclude && hasReflectionAt(it) }
    }

    override fun PrintStream.part1(lines: List<String>) = println(parse(lines).sumOf {
        (findReflection(it) ?: 0) * 100 + (findReflection(it.rotate()) ?: 0)
    })

    override fun PrintStream.part2(lines: List<String>) {
        var sum = 0

        outer@ for (pattern in parse(lines)) {
            val oldH = findReflection(pattern)
            val oldV = findReflection(pattern.rotate())

            for (i in pattern.indices) for (j in pattern[0].indices) {
                val newPattern = pattern.mapIndexed { ii, line ->
                    if (ii == i) buildString {
                        for ((jj, char) in line.withIndex()) append(
                            if (jj == j) {
                                if (char == '.') '#' else '.'
                            } else char
                        )
                    } else line
                }
                val h = findReflection(newPattern, oldH ?: -1)
                val v = findReflection(newPattern.rotate(), oldV ?: -1)

                if (h != null) {
                    sum += h * 100
                    continue@outer
                }
                if (v != null) {
                    sum += v
                    continue@outer
                }
            }
        }

        println(sum)
    }
}