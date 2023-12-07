package aoc

import Day
import java.io.PrintStream

class Day5 : Day() {
    private class Map(val destinationStart: Long, val sourceStart: Long, val length: Long) {
        operator fun contains(number: Long) = number >= sourceStart && number < sourceStart + length

        fun convert(number: Long) = number - sourceStart + destinationStart

        companion object {
            val default = Map(0, 0, Long.MAX_VALUE / 2)
        }
    }

    private fun parse(lines: List<String>) = lines[0].removePrefix("seeds: ").split(' ').map(String::toLong) to buildList {
        var i = 3
        while (i < lines.size) add(buildList {
            while (i < lines.size && lines[i].isNotEmpty()) {
                val (a, b, c) = lines[i++].split(' ').map(String::toLong)
                add(Map(a, b, c))
            }
            i += 2
        })
    }

    private fun solveSeed(seed: Long, list: List<List<Map>>) = list.fold(seed) { num, maps -> (maps.find { num in it } ?: Map.default).convert(num) }

    override fun PrintStream.part1(lines: List<String>) {
        val (seeds, steps) = parse(lines)
        println(seeds.minOf { solveSeed(it, steps) })
    }

    override fun PrintStream.part2(lines: List<String>) {
        val (seedRanges, steps) = parse(lines)
        var min = Long.MAX_VALUE
        var i = 0
        while (i < seedRanges.size) {
            val start = seedRanges[i++]
            val length = seedRanges[i++]
            repeat(length.toInt()) { num ->
                solveSeed(start + num, steps).takeIf { it < min }?.let { min = it }
            }
        }
        println(min)
    }
}