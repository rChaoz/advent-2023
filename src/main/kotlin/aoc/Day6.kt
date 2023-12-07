package aoc

import Day
import java.io.PrintStream

class Day6 : Day() {
    private fun parse1(lines: List<String>): List<Pair<Long, Long>> {
        val getNumbers: String.() -> List<Long> = { split(' ').filter(String::isNotEmpty).drop(1).map(String::toLong) }
        return lines[0].getNumbers() zip lines[1].getNumbers()
    }

    private fun parse2(lines: List<String>): Pair<Long, Long> =
        lines[0].filterNot(Char::isWhitespace).removePrefix("Time:").toLong() to
                lines[1].filterNot(Char::isWhitespace).removePrefix("Distance:").toLong()

    private fun countWinners(time: Long, distance: Long) = (0..time).count { waitTime -> waitTime * (time - waitTime) > distance }

    override fun PrintStream.part1(lines: List<String>) =
        println(parse1(lines).fold(1) { acc, (time, distance) -> countWinners(time, distance) * acc })

    override fun PrintStream.part2(lines: List<String>) = parse2(lines).let { (time, distance) -> println(countWinners(time, distance)) }
}