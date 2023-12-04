package aoc

import Day
import java.io.PrintStream

class Day4 : Day() {
    private data class Card(val id: Int, val winning: List<Int>, val numbers: List<Int>) {
        val win = winning.toSet().let { set -> numbers.count { it in set } }

        val score = (1 shl win) shr 1
    }

    private fun parse(lines: List<String>) = lines.map { line ->
        val (cardTitle, cardContent) = line.split(": ")
        val (winningStr, numbersStr) = cardContent.split(" | ")
        val winningNumbers = winningStr.split(" ").filter(String::isNotBlank).map(String::toInt)
        val cardNumbers = numbersStr.split(" ").filter(String::isNotBlank).map(String::toInt)
        Card(cardTitle.removePrefix("Card").trim().toInt(), winningNumbers, cardNumbers)
    }

    override fun PrintStream.part1(lines: List<String>) = println(parse(lines).sumOf(Card::score))

    override fun PrintStream.part2(lines: List<String>) {
        val cards = parse(lines)
        val wins = MutableList(lines.size) { 0 }
        var sum = wins.size
        for (i in cards.indices.reversed()) {
            val win = cards[i].win
            wins[i] += win
            for (j in 1..win) wins[i] += wins[i + j]
            sum += wins[i]
        }
        println(sum)
    }
}