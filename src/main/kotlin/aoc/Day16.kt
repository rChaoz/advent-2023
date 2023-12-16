package aoc

import Day
import java.io.PrintStream

class Day16 : Day() {
    // refelction1 for /, reflection2 for \
    private enum class Direction(val dx: Int, val dy: Int, reflection1: Int, reflection2: Int) {
        Up(-1, 0, 1, 3),
        Right(0, 1, 0, 2),
        Down(1, 0, 3, 1),
        Left(0, -1, 2, 0);

        val reflection by lazy { mapOf('/' to entries[reflection1], '\\' to entries[reflection2]) }

    }

    private fun travel(
        lines: List<String>, map: List<List<MutableSet<Direction>>>,
        startX: Int, startY: Int, startDirection: Direction, callback: (Char, Int, Int) -> Unit = { _, _, _ -> }
    ) {
        var x = startX
        var y = startY
        var direction = startDirection
        while (x in lines.indices && y in lines[0].indices) {
            val char = lines[x][y]
            if (!map[x][y].add(direction)) return
            callback(char, x, y)
            when (char) {
                '/', '\\' -> direction = direction.reflection[char]!!
                '-' -> if (direction == Direction.Up || direction == Direction.Down) {
                    travel(lines, map, x, y, Direction.Left, callback)
                    travel(lines, map, x, y, Direction.Right, callback)
                    return
                }

                '|' -> if (direction == Direction.Left || direction == Direction.Right) {
                    travel(lines, map, x, y, Direction.Up, callback)
                    travel(lines, map, x, y, Direction.Down, callback)
                    return
                }
            }
            x += direction.dx
            y += direction.dy
        }
    }

    private fun calcEnergise(lines: List<String>, x: Int, y: Int, direction: Direction): Int {
        val map = List(lines.size) { List(lines[0].length) { mutableSetOf<Direction>() } }
        travel(lines, map, x, y, direction)
        return map.sumOf { line -> line.count { it.isNotEmpty() } }
    }

    override fun PrintStream.part1(lines: List<String>) = println(calcEnergise(lines, 0, 0, Direction.Right))

    override fun PrintStream.part2(lines: List<String>) {
        val sides = listOf(
            Direction.Right to lines.indices.map { it to 0 },
            Direction.Up to lines[0].indices.map { lines.lastIndex to it },
            Direction.Left to lines.indices.map { it to lines[0].lastIndex },
            Direction.Down to lines.indices.map { 0 to it },
        )
        println(sides.maxOf { (dir, positions) -> positions.maxOf { (x, y) -> calcEnergise(lines, x, y, dir) } })
    }
}