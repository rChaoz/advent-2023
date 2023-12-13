package aoc

import Day
import java.io.PrintStream
import java.util.concurrent.atomic.AtomicLong

class Day12 : Day() {
    private fun parse(lines: List<String>) = lines.map { line ->
        val (conditions, groups) = line.split(' ')
        conditions to groups.split(',').map(String::toInt)
    }

    private fun unfold(lines: List<Pair<String, List<Int>>>) = lines.map { (conditions, groups) ->
        buildString {
            append(conditions)
            repeat(4) {
                append('?')
                append(conditions)
            }
        } to List(5) { groups }.flatten()
    }

    private infix fun String.matchesPattern(pattern: String) = this.padEnd(pattern.length, '.').zip(pattern).all { (a, b) -> a == b || b == '?' }

    override fun PrintStream.part1(lines: List<String>) = println(parse(lines).sumOf { (conditionPattern, groups) ->
        val spaceLeft = conditionPattern.length + 1 - groups.size - groups.sum()
        fun backtrack(spacing: List<Int>): Int {
            if (spacing.size < groups.size) return (0..spaceLeft - spacing.sum()).sumOf { backtrack(spacing + it) }
            val string = buildString {
                for (i in groups.indices) {
                    if (i != 0) append('.')
                    repeat(spacing[i]) { append('.') }
                    repeat(groups[i]) { append('#') }
                }
                if (length > conditionPattern.length) return 0
            }
            return if (string matchesPattern conditionPattern) 1 else 0
        }

        backtrack(emptyList())
    })

    override fun PrintStream.part2(lines: List<String>) = println(unfold(parse(lines)).sumOf { (conditionPattern, groups) ->
        val memo = mutableMapOf<Pair<Int, Int>, Long>()

        fun backtrack(startIndex: Int, groupIndex: Int): Long {
            if (groupIndex >= groups.size) {
                return if (startIndex > conditionPattern.length || conditionPattern.substring(startIndex - 1).all { it == '?' || it == '.' }) 1
                else 0
            }

            memo[startIndex to groupIndex]?.let { return it }

            val size = groups[groupIndex]
            val more = groups.lastIndex - groupIndex + groups.subList(groupIndex + 1, groups.size).sum()
            var index = startIndex

            if (size + index + more > conditionPattern.length) return 0

            var count = 0L
            while (size + index + more <= conditionPattern.length) {
                if (
                    (0..<size).all { conditionPattern[index + it] == '#' || conditionPattern[index + it] == '?' } &&
                    (more == 0 || conditionPattern[index + size] == '.' || conditionPattern[index + size] == '?')
                ) count += backtrack(index + size + 1, groupIndex + 1)

                if ((conditionPattern[index] == '.' || conditionPattern[index] == '?')) ++index
                else break
            }

            memo[startIndex to groupIndex] = count
            return count
        }

        backtrack(0, 0)
    })
}
