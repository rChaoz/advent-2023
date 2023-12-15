package aoc

import Day
import java.io.PrintStream

private typealias Map = Array<CharArray>

class Day14 : Day() {
    private enum class Pos(val char: Char) {
        Rock('O'), Wall('#'), Empty('.');

        companion object {
            fun fromChar(char: Char) = entries.first { it.char == char }
        }
    }

    private enum class Direction {
        Up, Right, Down, Left
    }

    private fun parse(lines: List<String>): Map = Array(lines.size) { i -> lines[i].toCharArray() }

    private fun shift(map: Map, direction: Direction) {
        val indices = when (direction) {
            Direction.Up -> map.indices
            Direction.Right -> map[0].indices.reversed()
            Direction.Down -> map.indices.reversed()
            Direction.Left -> map[0].indices
        }
        val secondaryIndices = when (direction) {
            Direction.Up, Direction.Down -> map[0].indices
            Direction.Left, Direction.Right -> map.indices
        }
        val initialPlaceIndex = when (direction) {
            Direction.Up -> 0
            Direction.Right -> map[0].lastIndex
            Direction.Down -> map.lastIndex
            Direction.Left -> 0
        }
        val delta = when (direction) {
            Direction.Up, Direction.Left -> 1
            Direction.Down, Direction.Right -> -1
        }
        val get: (index: Int, secondary: Int) -> Char = when (direction) {
            Direction.Up, Direction.Down -> { i, s -> map[i][s] }
            Direction.Left, Direction.Right -> { i, s -> map[s][i] }
        }
        val set: (index: Int, secondary: Int, char: Char) -> Unit = when (direction) {
            Direction.Up, Direction.Down -> { i, s, c -> map[i][s] = c }
            Direction.Left, Direction.Right -> { i, s, c -> map[s][i] = c }
        }

        for (secondary in secondaryIndices) {
            var placeIndex = initialPlaceIndex
            for (i in indices) {
                when (get(i, secondary)) {
                    '#' -> placeIndex = i + delta
                    'O' -> {
                        set(i, secondary, '.')
                        set(placeIndex, secondary, 'O')
                        placeIndex += delta
                    }
                }
            }
        }
    }

    private fun cycle(map: Map) {
        shift(map, Direction.Up)
        shift(map, Direction.Left)
        shift(map, Direction.Down)
        shift(map, Direction.Right)
    }

    private fun Map.copy() = Array(size) { this@copy[it].copyOf() }

    private fun Map.calculateLoad(): Int {
        var sum = 0
        var mul = size
        for (line in this) {
            sum += line.count { it == 'O' } * mul
            --mul
        }
        return sum
    }

    override fun PrintStream.part1(lines: List<String>) = println(parse(lines).also { shift(it, Direction.Up) }.calculateLoad())

    override fun PrintStream.part2(lines: List<String>) {
        val map = parse(lines)
        val memos = mutableMapOf<Int, Pair<Int, Map>>()
        var cycles = 0
        val totalCycles = 1_000_000_000

        do {
            cycle(map)
            ++cycles

            val hash = map.contentDeepHashCode()
            val match = memos[hash]
            if (match != null) {
                val (oldCycles, oldMap) = match
                if (map.contentDeepEquals(oldMap)) {
                    val diff = cycles - oldCycles
                    cycles += (totalCycles - cycles) / diff * diff
                }
            }
            memos[hash] = cycles to map.copy()
        } while (cycles < totalCycles)
        println(map.calculateLoad())
    }
}