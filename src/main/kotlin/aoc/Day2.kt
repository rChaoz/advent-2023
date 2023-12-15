package aoc

import Day
import java.io.PrintStream
import kotlin.math.max

class Day2 : Day() {
    private data class Game(val id: Int, val reveals: List<Reveal>)

    private data class Reveal(val red: Int, val green: Int, val blue: Int)

    private fun parse(lines: List<String>) = lines.map { line ->
        val (game, cubesString) = line.split(": ")
        val reveals = cubesString.split("; ").map {
            var red = 0
            var green = 0
            var blue = 0
            for (item in it.split(", ")) {
                val (amount, color) = item.split(" ")
                when (color) {
                    "red" -> red = amount.toInt()
                    "green" -> green = amount.toInt()
                    "blue" -> blue = amount.toInt()
                    else -> throw RuntimeException("Unknown color: $color")
                }
            }
            Reveal(red, green, blue)
        }
        Game(game.removePrefix("Game ").toInt(), reveals)
    }

    override fun PrintStream.part1(lines: List<String>) = println(parse(lines).filter { game ->
        game.reveals.none { it.red > 12 || it.green > 13 || it.blue > 14 }
    }.sumOf(Game::id))

    override fun PrintStream.part2(lines: List<String>) = println(parse(lines).sumOf { game ->
        game.reveals.fold(Reveal(0, 0, 0)) { acc, reveal ->
            Reveal(max(acc.red, reveal.red), max(acc.green, reveal.green), max(acc.blue, reveal.blue))
        }.let { it.red * it.green * it.blue }
    })
}