package aoc

import Day
import java.io.PrintStream
import java.util.*

private typealias HeatMap = List<List<Int>>

class Day17 : Day() {
    private fun parse(lines: List<String>) = lines.map { it.map(Char::digitToInt) }

    private enum class Direction(val dx: Int, val dy: Int) {
        Up(-1, 0), Right(0, 1), Down(1, 0), Left(0, -1);

        val opposite by lazy {
            when (this) {
                Up -> Down
                Right -> Left
                Down -> Up
                Left -> Right
            }
        }
    }

    private data class State(val x: Int, val y: Int, val heatLoss: Int, val direction: Direction, val length: Int) : Comparable<State> {
        override fun compareTo(other: State) = heatLoss.compareTo(other.heatLoss)
    }

    private data class MapState(val direction: Direction, val length: Int)

    @Suppress("SameParameterValue")
    private fun dijkstra(
        map: HeatMap, startX: Int, startY: Int, startDirection: Direction, destX: Int, destY: Int,
        maxStraightLine: Int, blocksAheadNeeded: Int
    ): Int {
        val queue = PriorityQueue<State>()
        queue.add(State(startX, startY, 0, startDirection, 0))
        val stateMap = MutableList(map.size) { MutableList(map[0].size) { mutableMapOf<MapState, Int>() } }
        val xIndices = map.indices
        val yIndices = map[0].indices

        while (queue.isNotEmpty()) {
            val (x, y, heatLoss, direction, length) = queue.remove()
            val directions = if (length <= blocksAheadNeeded) listOf(direction)
            else buildList {
                addAll(Direction.entries)
                if (length == maxStraightLine) this -= direction
                this -= direction.opposite
            }
            for (newDirection in directions) {
                val newX = x + newDirection.dx
                val newY = y + newDirection.dy
                if (newX !in xIndices || newY !in yIndices) continue

                val newHeatLoss = heatLoss + map[newX][newY]
                val newLength = if (direction == newDirection) length + 1 else 1
                val newState = MapState(newDirection, newLength)
                val currentHeatLoss = stateMap[newX][newY][newState] ?: Int.MAX_VALUE
                if (currentHeatLoss <= newHeatLoss) continue

                queue.add(State(newX, newY, newHeatLoss, newDirection, newLength))
                stateMap[newX][newY][newState] = newHeatLoss
            }
        }
        return stateMap[destX][destY].minOf { (state, value) -> if (state.length <= blocksAheadNeeded) Int.MAX_VALUE else value }
    }

    private fun PrintStream.solve(lines: List<String>, maxStraightLine: Int, blocksAheadNeeded: Int) = parse(lines).let { map ->
        println(dijkstra(map, 0, 0, Direction.Right, map.lastIndex, map[0].lastIndex, maxStraightLine, blocksAheadNeeded))
    }


    override fun PrintStream.part1(lines: List<String>) = solve(lines, 3, 0)

    override fun PrintStream.part2(lines: List<String>) = solve(lines, 10, 3)
}