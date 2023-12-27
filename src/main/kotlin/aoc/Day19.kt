package aoc

import Day
import split
import java.io.PrintStream

private typealias Part = Map<String, Int>

class Day19 : Day() {
    private companion object {
        const val ACCEPTED = "A"
        const val REJECTED = "R"
    }

    private data class Workflow(val rules: List<Rule>, val defaultTarget: String) {
        fun execute(part: Part) = rules.firstOrNull { it.check(part) }?.target ?: defaultTarget
    }

    private data class Rule(val id: String, val op: Op, val value: Int, val target: String) {
        enum class Op(val char: Char) {
            Less('<'), Greater('>'), Equal('=');

            companion object {
                fun from(char: Char) = entries.first { it.char == char }
            }
        }

        fun check(value: Int) = when (op) {
            Op.Less -> value < this.value
            Op.Greater -> value > this.value
            Op.Equal -> value == this.value
        }

        fun check(part: Part) = check(part[id]!!)
    }

    private fun parse(lines: List<String>): Pair<Map<String, Workflow>, List<Part>> {
        val workflowRegex = Regex("""(\w+)\{(.*)}""")
        val ruleRegex = Regex("""(\w+)([<>=])(-?\d+):(\w+)""")
        val (workflows, parts) = lines.split(String::isEmpty)

        return workflows.associate { workflow ->
            val (name, rulesString) = workflowRegex.matchEntire(workflow)!!.destructured
            val rules = rulesString.split(',')
            name to Workflow(rules.dropLast(1).map { rule ->
                val (id, op, value, target) = ruleRegex.matchEntire(rule)!!.destructured
                Rule(id, Rule.Op.from(op[0]), value.toInt(), target)
            }, rules.last())
        } to parts.map { part ->
            val properties = part.substring(1, part.length - 1).split(',')
            properties.associate { property ->
                val (name, value) = property.split('=')
                name to value.toInt()
            }
        }
    }

    override fun PrintStream.part1(lines: List<String>) {
        val (workflows, parts) = parse(lines)

        var sum = 0
        for (part in parts) {
            var workflow = "in"
            while (workflow != ACCEPTED && workflow != REJECTED)
                workflow = workflows[workflow]!!.execute(part)
            if (workflow == ACCEPTED) sum += part.values.sum()
        }

        println(sum)
    }

    override fun PrintStream.part2(lines: List<String>) {
        val (workflows) = parse(lines)

        var sum = 0L

        fun backtrack(part: Map<String, List<Int>>, workflow: String) {
            if (workflow == REJECTED || part.values.any(List<Int>::isEmpty)) return
            if (workflow == ACCEPTED) {
                sum += part.values.fold(1L) { acc, list -> acc * list.size }
                return
            }

            var p = part
            val w = workflows[workflow]!!

            for (rule in w.rules) {
                val (filtered, rest) = p[rule.id]!!.partition(rule::check)
                if (filtered.isNotEmpty()) backtrack(p + (rule.id to filtered), rule.target)
                if (rest.isEmpty()) return
                p = p + (rule.id to rest)
            }

            backtrack(p, w.defaultTarget)
        }

        backtrack(listOf("x", "m", "s", "a").associateWith { (1..4000).toList() }, "in")

        println(sum)
    }
}

