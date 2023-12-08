package aoc

import Day
import java.io.PrintStream

class Day8 : Day() {
    private val regex = Regex("""(.{3}) = \((.{3}), (.{3})\)""")

    private fun parse(lines: List<String>) = lines[0] to lines.subList(2, lines.size).associate { line ->
        val (source, left, right) = regex.matchEntire(line)!!.destructured
        source to mapOf('L' to left, 'R' to right)
    }

    private fun gcd(first: Long, second: Long): Long {
        var a = first
        var b = second
        while (a > 0 && b > 0) {
            val r = a % b
            a = b
            b = r
        }
        return if (a > 0) a else b
    }

    private fun lcm(first: Long, second: Long) = first * second / gcd(first, second)

    private fun lcm(nums: List<Long>): Long = nums.reduce(::lcm)

    override fun PrintStream.part1(lines: List<String>) {
        val (instructions, map) = parse(lines)
        var steps = 0
        var current = "AAA"
        while (true) {
            instructions.forEach { instruction ->
                current = map[current]!![instruction]!!
                ++steps
                if (current == "ZZZ") {
                    println(steps)
                    return
                }
            }
        }
    }

    private fun generateAllPossibilities(nodes: List<List<Long>>): List<List<Long>> {
        val first = nodes[0]
        val rest = nodes.subList(1, nodes.size)
        if (rest.isEmpty()) return first.map { listOf(it) }
        return first.flatMap { num -> generateAllPossibilities(rest).map { listOf(num) + it } }
    }

    override fun PrintStream.part2(lines: List<String>) {
        val (instructions, map) = parse(lines)
        val set = mutableSetOf<Pair<String, Int>>()
        val nodes = map.keys.filter { it.endsWith('A') }.map { node ->
            set.clear()
            var current = node to 0
            var steps = 0L
            buildList {
                while (current !in set) {
                    set += current
                    if (current.first.endsWith('Z')) add(steps)
                    ++steps
                    current = map[current.first]!![instructions[current.second]]!! to (current.second + 1) % instructions.length
                }
            }
        }
        println(generateAllPossibilities(nodes).minOf(::lcm))
    }
}