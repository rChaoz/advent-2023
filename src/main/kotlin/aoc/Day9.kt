package aoc

import Day
import java.io.PrintStream

class Day9 : Day() {
    private fun parse(lines: List<String>) = lines.map { it.split(' ').map(String::toInt) }

    private fun diff(list: List<Int>) = List(list.size - 1) { i -> list[i + 1] - list[i] }

    private fun diffs(list: List<Int>): List<List<Int>> {
        val diffs = mutableListOf(list)
        while (diffs.last().any { it != 0 }) diffs += diff(diffs.last())
        return diffs
    }

    private fun solve1(list: List<Int>) = diffs(list).sumOf { it.last() }

    private fun solve2(list: List<Int>) = diffs(list).foldRight(0) { l, acc -> l.first() - acc }

    override fun PrintStream.part1(lines: List<String>) =
        println(parse(lines).map(::solve1).sum())

    override fun PrintStream.part2(lines: List<String>) =
        println(parse(lines).map(::solve2).sum())
}