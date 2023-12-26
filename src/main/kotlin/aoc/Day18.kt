package aoc

import Day
import java.io.PrintStream
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlin.math.max
import kotlin.math.min

class Day18 : Day() {
    private enum class Direction(val char: Char, val dx: Int, val dy: Int) {
        Right('R', 0, 1),
        Down('D', 1, 0),
        Left('L', 0, -1),
        Up('U', -1, 0);

        companion object {
            fun fromChar(char: Char) = entries.first { it.char == char }
        }
    }

    private fun parse(lines: List<String>, part2: Boolean) = lines.map {
        val (direction, distance, colorRaw) = it.split(' ')
        val color = colorRaw.substring(2, colorRaw.length - 1)
        if (part2) Direction.entries[color.last().digitToInt()] to color.dropLast(1).toInt(16)
        else Direction.fromChar(direction[0]) to distance.toInt()
    }

    private class Map {
        companion object {
            private const val SIZE = 2000
        }

        var xMin = 0
        var xMax = 0
        var yMin = 0
        var yMax = 0

        private val map = Array(SIZE) { Array(SIZE) { 0 } }

        operator fun get(x: Int, y: Int) = map[x + SIZE / 2][y + SIZE / 2]

        operator fun set(x: Int, y: Int, value: Int) {
            if (x < xMin) xMin = x
            if (x > xMax) xMax = x
            if (y < yMin) yMin = y
            if (y > yMax) yMax = y
            map[x + SIZE / 2][y + SIZE / 2] = value
        }
    }

    override fun PrintStream.part1(lines: List<String>) {
        val map = Map()
        var count = 0

        var x = 0
        var y = 0
        for ((direction, distance) in parse(lines, false)) repeat(distance) {
            ++count
            x += direction.dx
            y += direction.dy
            map[x, y] = 1
        }

        fun bfs(startX: Int, startY: Int) {
            map[startX, startY] = 1
            ++count
            val queue = ArrayDeque<Pair<Int, Int>>()
            queue.add(startX to startY)
            while (queue.isNotEmpty()) {
                val (qx, qy) = queue.removeFirst()
                for (dir in Direction.entries) {
                    val newX = qx + dir.dx
                    val newY = qy + dir.dy
                    if (map[newX, newY] == 0) {
                        ++count
                        map[newX, newY] = 1
                        queue.add(newX to newY)
                    }
                }
            }
        }

        for (j in map.yMin..map.yMax) {
            var i = map.xMin
            while (map[i, j] == 0) ++i
            ++i
            if (map[i, j] == 0) {
                bfs(i, j)
                break
            }
        }

        println(count)
    }

    override fun PrintStream.part2(lines: List<String>) {
        data class Command(val direction: Direction, var x1: Int, var x2: Int, val y: Int, val before: Direction?, val after: Direction?)

        val rawCommands = parse(lines, true)
        var sum = 0L

        var goDirection = Direction.Down
        val allCommands = buildList {
            var x = 0
            var y = 0
            var minX = Int.MAX_VALUE
            // Find left-most UP/DOWN move
            for (index in rawCommands.indices) {
                val (direction, distance) = rawCommands[index]
                val oldX = x
                sum += distance
                x += direction.dx * distance
                y += direction.dy * distance

                if (direction == Direction.Up || direction == Direction.Down) {
                    val before = rawCommands[if (index == 0) rawCommands.lastIndex else index - 1].first
                    val after = rawCommands[if (index == rawCommands.lastIndex) 0 else index + 1].first
                    this += if (oldX < x) Command(direction, oldX, x, y, before, after) else Command(direction, x, oldX, y, before, after)
                    if (x < minX) {
                        minX = x
                        goDirection = direction
                    }
                }
            }
        }.sortedBy { it.y }

        allCommands.forEach { c ->
            if (c.direction == goDirection) {
                if (c.direction == Direction.Up) {
                    if (c.after == Direction.Right) c.x1++
                    if (c.before == Direction.Left) c.x2--
                } else {
                    if (c.before == Direction.Left) c.x1++
                    if (c.after == Direction.Right) c.x2--
                }
            }
        }
        val (goCommands, matchingCommands) = allCommands.partition { it.direction == goDirection }

        val goQueue = ArrayDeque(goCommands)
        val matching = LinkedList(matchingCommands)

        while (goQueue.isNotEmpty()) {
            val (direction, x1, x2, y) = goQueue.removeFirst()
            val (_, matchX1, matchX2, matchY) = matching.first { it.y >= y && it.x1 <= x2 && it.x2 >= x1 }

            val area = (matchY - y - 1L) * (min(x2, matchX2) - max(x1, matchX1) + 1L)
            sum += area

            if (x1 < matchX1) goQueue.addFirst(Command(direction, x1, matchX1 - 1, y, null, null))
            if (x2 > matchX2) goQueue.addFirst(Command(direction, matchX2 + 1, x2, y, null, null))
        }

        println(sum)
    }
}