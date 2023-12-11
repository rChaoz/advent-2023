package aoc

import Day
import Matrix
import MutableMatrix
import Point
import find
import get
import set
import java.io.PrintStream

class Day10 : Day() {
    private enum class Direction(val di: Int, val dj: Int) {
        Top(-1, 0), Right(0, 1), Bottom(1, 0), Left(0, -1);

        val opposite by lazy {
            when (this) {
                Top -> Bottom
                Right -> Left
                Bottom -> Top
                Left -> Right
            }
        }

        val rotatedLeft by lazy {
            when (this) {
                Top -> Left
                Right -> Top
                Bottom -> Right
                Left -> Bottom
            }
        }

        val rotatedRight by lazy {
            when (this) {
                Top -> Right
                Right -> Bottom
                Bottom -> Left
                Left -> Top
            }
        }

        fun transform(point: Point) = (point.first + di) to (point.second + dj)
    }

    private enum class Pipe(val char: Char, val niceChar: Char, val directions: List<Direction>) {
        LR('-', '─', listOf(Direction.Left, Direction.Right)),
        TB('|', '│', listOf(Direction.Top, Direction.Bottom)),

        TL('J', '┘', listOf(Direction.Left, Direction.Top)),
        TR('L', '└', listOf(Direction.Top, Direction.Right)),
        BR('F', '┌', listOf(Direction.Right, Direction.Bottom)),
        BL('7', '┐', listOf(Direction.Left, Direction.Bottom)),

        START('S', 'S', Direction.entries),
        GROUND('.', '.', emptyList());

        companion object {
            fun from(char: Char) = entries.first { it.char == char }
        }
    }

    private fun parse(lines: List<String>) = mutableListOf<MutableList<Pipe>>().apply {
        val length = lines[0].length + 2
        add(MutableList(length) { Pipe.GROUND })
        addAll(lines.map { line ->
            mutableListOf<Pipe>().apply {
                add(Pipe.GROUND)
                addAll(line.map(Pipe::from))
                add(Pipe.GROUND)
            }
        })
        add(MutableList(length) { Pipe.GROUND })
    }.let { map ->
        // Find start
        val start = map.find(Pipe.START)
        // Replace start with correct pipe
        val directions = Direction.entries.associateWith {
            it.opposite in map[it.transform(start)].directions
        }
        if (directions.values.count { it } != 2) throw IllegalStateException("Invalid start")
        map[start] = when {
            directions[Direction.Top]!! && directions[Direction.Left]!! -> Pipe.TL
            directions[Direction.Top]!! && directions[Direction.Right]!! -> Pipe.TR
            directions[Direction.Bottom]!! && directions[Direction.Left]!! -> Pipe.BL
            directions[Direction.Bottom]!! && directions[Direction.Right]!! -> Pipe.BR
            else -> throw IllegalStateException("Unknown corner: $directions")
        }
        start to map
    }

    private inline fun followLine(
        map: Matrix<Pipe>, start: Point, direction: Direction? = null, includeStart: Boolean = false,
        onPipe: (Point, Direction) -> Unit
    ) {
        var dir = direction
        if (dir == null) dir = Direction.entries.firstNotNullOf {
            it.takeIf { it.opposite in map[it.transform(start)].directions }
        }
        if (includeStart) onPipe(start, dir)
        var p = dir.transform(start)

        var prev = start
        while (p != start) {
            dir = map[p].directions.firstNotNullOf {
                val new = it.transform(p)
                it.takeIf { new != prev && it.opposite in map[new].directions }
            }
            onPipe(p, dir)
            prev = p
            p = dir.transform(p)
        }
    }

    override fun PrintStream.part1(lines: List<String>) {
        var length = 0
        val (start, map) = parse(lines)
        followLine(map, start) { _, _ -> ++length }
        println((length + 1) / 2)
    }

    enum class MapArea {
        None, Pipe, Outside, Inside;
    }

    override fun PrintStream.part2(lines: List<String>) {
        val (start, map) = parse(lines)

        val areaMap: MutableMatrix<MapArea> = MutableList(map.size) { MutableList(map[0].size) { MapArea.None } }
        followLine(map, start, includeStart = true) { p, _ -> areaMap[p] = MapArea.Pipe }

        fun bfs(point: Point, value: MapArea) {
            if (areaMap[point] != MapArea.None) return
            areaMap[point] = value

            if (value == MapArea.None) throw IllegalArgumentException("Can't bfs() set to None")
            val queue = ArrayDeque<Point>().apply { add(point) }
            while (queue.isNotEmpty()) {
                val initial = queue.removeFirst()
                for (dir in Direction.entries) {
                    val p = dir.transform(initial)
                    if (p.first < 0 || p.second < 0 || p.first >= map.size || p.second >= map[0].size || areaMap[p] != MapArea.None) continue
                    areaMap[p] = value
                    queue.add(p)
                }
            }
        }

        bfs(0 to 0, MapArea.Outside)

        var current = Pipe.GROUND
        fun findOutside(): Triple<Point, Boolean, Direction> {
            followLine(map, start) { p, dir ->
                val pipe = map[p]
                if (current != Pipe.GROUND) {
                    if (pipe == Pipe.LR) {
                        if (areaMap[Direction.Top.transform(p)] == MapArea.Outside) return Triple(p, (dir == Direction.Right), dir)
                        if (areaMap[Direction.Bottom.transform(p)] == MapArea.Outside) return Triple(p, (dir == Direction.Left), dir)
                    } else if (pipe == Pipe.TB) {
                        if (areaMap[Direction.Left.transform(p)] == MapArea.Outside) return Triple(p, (dir == Direction.Top), dir)
                        if (areaMap[Direction.Right.transform(p)] == MapArea.Outside) return Triple(p, (dir == Direction.Bottom), dir)
                    }
                }
                current = pipe
            }
            throw RuntimeException("Outside not found")
        }

        val (outsideStart, outsideIsLeft, startDir) = findOutside()
        followLine(map, outsideStart, startDir, includeStart = true) { p, dir ->
            val pipe = map[p]
            when (pipe) {
                Pipe.LR, Pipe.TB -> {
                    val outside = if (outsideIsLeft) dir.rotatedLeft else dir.rotatedRight
                    bfs(outside.transform(p), MapArea.Outside)
                    bfs(outside.opposite.transform(p), MapArea.Inside)
                }

                Pipe.TL, Pipe.TR, Pipe.BL, Pipe.BR -> {
                    val what = when (dir) {
                        Direction.Top -> if ((pipe == if (outsideIsLeft) Pipe.TR else Pipe.TL)) MapArea.Outside else MapArea.Inside
                        Direction.Bottom -> if ((pipe == if (outsideIsLeft) Pipe.BL else Pipe.BR)) MapArea.Outside else MapArea.Inside
                        Direction.Right -> if ((pipe == if (outsideIsLeft) Pipe.BR else Pipe.TR)) MapArea.Outside else MapArea.Inside
                        Direction.Left -> if ((pipe == if (outsideIsLeft) Pipe.TL else Pipe.BL)) MapArea.Outside else MapArea.Inside
                    }
                    for (d in pipe.directions) bfs(d.opposite.transform(p), what)
                }

                Pipe.START -> return@followLine

                else -> throw RuntimeException("Unknown pipe: $pipe")
            }

            current = pipe
        }

        println(areaMap.sumOf { line -> line.count { it == MapArea.Inside } })
    }
}
