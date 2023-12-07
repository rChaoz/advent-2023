package aoc

import Day
import java.io.PrintStream
import java.lang.RuntimeException
import kotlin.math.min

private val cardToValue = (2..9).associateBy(Int::digitToChar) + mapOf(
    'T' to 10, 'J' to 12, 'Q' to 13, 'K' to 14, 'A' to 15
)

private val cardToValueJokers = cardToValue + mapOf('J' to 1)

private inline infix fun Int.then(other: () -> Int) = if (this == 0) other() else this

private operator fun <T : Comparable<T>> List<T>.compareTo(other: List<T>) =
    (0..<min(this.size, other.size)).firstNotNullOfOrNull { i -> this[i].compareTo(other[i]).takeIf { it != 0 } }
        ?: throw RuntimeException("Lists are not equal")

class Day7 : Day() {
    private class Hand(cards: List<Char>, val bet: Int, jokers: Boolean) : Comparable<Hand> {
        private fun List<Int>.group() = groupBy { it }.mapValues { (_, value) -> value.size }

        val cardValues = cards.map { (if (jokers) cardToValueJokers else cardToValue)[it]!! }

        val handType = cardValues.let { cardsValues ->
            val replacement = cardsValues.group().filterKeys { it != 1 }.maxByOrNull { it.value }?.key ?: 15
            cardsValues.map { if (it == 1) replacement else it }
        }.group().let { cardGroups ->
            when (cardGroups.size) {
                1 -> 7 // five of a kind
                2 -> {
                    if (cardGroups.values.max() == 4) 6 // four of a kind
                    else 5 // full house
                }
                3 -> {
                    if (cardGroups.values.max() == 3) 4 // three of a kind
                    else 3 // 2 pairs
                }
                4 -> 2 // 1 pair
                else -> 1 // high card
            }
        }

        override fun compareTo(other: Hand) = handType.compareTo(other.handType) then { cardValues.compareTo(other.cardValues) }
    }

    private fun parse(lines: List<String>, jokers: Boolean = false) = lines.map { line ->
        val (cards, bid) = line.split(' ')
        Hand(cards.toList(), bid.toInt(), jokers)
    }

    override fun PrintStream.part1(lines: List<String>) =
        println(parse(lines).sorted().mapIndexed { index, hand -> (index + 1) * hand.bet }.sum())

    override fun PrintStream.part2(lines: List<String>) =
        println(parse(lines, true).sorted().mapIndexed { index, hand -> (index + 1) * hand.bet }.sum())
}