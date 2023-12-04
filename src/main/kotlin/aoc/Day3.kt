package aoc

import Day
import java.io.PrintStream

class Day3 : Day() {
    private fun Char.isSymbol() = this != '.' && !this.isDigit()

    private fun solve(lines: List<String>): Pair<Int, Int> {
        val rows = buildList {
            val len = lines[0].length + 2
            add(List(len + 2) { '.' })
            for (line in lines) add(buildList(len) {
                add('.')
                for (char in line) add(char)
                add('.')
            })
            add(List(len + 2) { '.' })
        }

        var sum = 0
        val gears = mutableMapOf<Pair<Int, Int>, List<Int>>()

        rows.forEachIndexed { index, row ->
            var start = -1
            var num = 0
            row.forEachIndexed { i, c ->
                if (start == -1) {
                    if (c.isDigit()) {
                        start = i
                        num = c.digitToInt()
                    }
                } else {
                    if (c.isDigit()) num = num * 10 + c.digitToInt()
                    else {
                        var symbol = false
                        for (y in listOf(index - 1, index + 1))
                            for (x in start - 1..i) {
                                if (!rows[y][x].isSymbol()) continue
                                symbol = true
                                if (rows[y][x] == '*') gears.merge(y to x, listOf(num), List<Int>::plus)
                            }
                        for (x in listOf(start - 1, i)) {
                            if (!row[x].isSymbol()) continue
                            symbol = true
                            if (row[x] == '*') gears.merge(index to x, listOf(num), List<Int>::plus)
                        }
                        if (symbol) sum += num
                        start = -1
                    }
                }
            }
        }

        return sum to gears.mapNotNull { gear -> gear.value.takeIf { it.size == 2 }?.let { it[0] * it[1] } }.sum()
    }

    override fun PrintStream.part1(lines: List<String>) = println(solve(lines).first)

    override fun PrintStream.part2(lines: List<String>) = println(solve(lines).second)
}