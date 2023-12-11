package aoc

import Day
import Point
import java.io.PrintStream
import kotlin.math.abs

class Day11 : Day() {
    private fun parse(lines: List<String>) = buildList {
        lines.forEachIndexed { i, line -> line.forEachIndexed { j, c -> if (c == '#') add(i to j) }}
    }

    private fun expandUniverse(galaxies: List<Point>, expansion: Long): List<Pair<Long, Long>> {
        val lineCount = galaxies.maxOf { it.first } + 1
        val columnCount = galaxies.maxOf { it.second } + 1

        val lines = MutableList(lineCount) { false }
        val columns = MutableList(columnCount) { false }

        galaxies.forEach { (i, j) -> lines[i] = true; columns[j] = true }

        @Suppress("DuplicatedCode")
        val lineExpansion = MutableList(lineCount) { 0L }
        lineExpansion[0] = if (!lines[0]) 1 else 0
        for (i in 1..<lineExpansion.size) lineExpansion[i] = lineExpansion[i - 1] + if (!lines[i]) expansion else 0

        val columnExpansion = MutableList(columnCount) { 0L }
        columnExpansion[0] = if (!columns[0]) 1 else 0
        for (j in 1..<columnExpansion.size) columnExpansion[j] = columnExpansion[j - 1] + if (!columns[j]) expansion else 0

        return galaxies.map { (i, j) -> (i + lineExpansion[i]) to (j + columnExpansion[j]) }
    }

    private fun solve(lines: List<String>, expansion: Long) {
        val galaxies = expandUniverse(parse(lines), expansion)
        println(galaxies)
        var sum = 0L
        for (i in 0..<galaxies.lastIndex) for (j in i + 1..galaxies.lastIndex) {
            val x = galaxies[i]
            val y = galaxies[j]
            sum += abs(x.first - y.first) + abs(x.second - y.second)
        }
        println(sum)
    }

    override fun PrintStream.part1(lines: List<String>) = solve(lines, 1)

    override fun PrintStream.part2(lines: List<String>) = solve(lines, 999_999)
}