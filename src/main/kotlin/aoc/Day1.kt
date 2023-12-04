package aoc

import Day
import java.io.PrintStream

class Day1 : Day() {
    override fun PrintStream.part1(lines: List<String>) = println(lines.sumOf {
        it.first(Char::isDigit).digitToInt() * 10 + it.last(Char::isDigit).digitToInt()
    })

    private val numbers =
        mapOf("one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9) +
                (1..9).associateBy { it.toString() }

    override fun PrintStream.part2(lines: List<String>) = println(lines.sumOf {
        numbers[it.findAnyOf(numbers.keys)!!.second]!! * 10 + numbers[it.findLastAnyOf(numbers.keys)!!.second]!!
    })
}