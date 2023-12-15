package aoc

import Day
import java.io.PrintStream

class Day15 : Day() {
    private fun parse(lines: List<String>) = lines[0].split(',')

    private fun String.hash() = fold(0) { acc, char -> (acc + char.code) * 17 % 256 }

    override fun PrintStream.part1(lines: List<String>) = println(parse(lines).sumOf { it.hash() })

    override fun PrintStream.part2(lines: List<String>) {
        val boxes = List(256) { LinkedHashMap<String, Int>() }
        for (command in parse(lines)) {
            if (command.endsWith('-')) {
                val label = command.dropLast(1)
                boxes[label.hash()].remove(label)
            } else {
                val (label, focalLength) = command.split('=')
                boxes[label.hash()][label] = focalLength.toInt()
            }
        }
        var sum = 0
        boxes.forEachIndexed { boxIndex, box ->
            box.values.forEachIndexed { index, focalLength ->
                sum += (boxIndex + 1) * (index + 1) * focalLength
            }
        }
        println(sum)
    }
}