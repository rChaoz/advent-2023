package aoc

import Day
import java.io.PrintStream

class Day20 : Day() {
    private data class Pulse(val source: String, val target: String, val high: Boolean)

    private sealed class Node(val targets: List<String>) {
        abstract fun processPulse(source: String, high: Boolean): List<Pair<String, Boolean>>

        protected fun createPulses(high: Boolean) = targets.map { it to high }
    }

    private class Broadcaster(targets: List<String>) : Node(targets) {
        override fun processPulse(source: String, high: Boolean) = createPulses(high)
    }

    private data object Simple : Node(emptyList()) {
        override fun processPulse(source: String, high: Boolean): List<Pair<String, Boolean>> = emptyList()
    }

    private class Output : Node(emptyList()) {
        var lowPulses = 0
            private set
        var highPulses = 0
            private set

        fun reset() {
            lowPulses = 0
            highPulses = 0
        }

        override fun processPulse(source: String, high: Boolean): List<Pair<String, Boolean>> {
            if (high) ++highPulses
            else ++lowPulses
            return emptyList()
        }
    }

    private class FlipFlop(targets: List<String>) : Node(targets) {
        var state = false

        override fun processPulse(source: String, high: Boolean): List<Pair<String, Boolean>> {
            if (high) return emptyList()
            state = !state
            return createPulses(state)
        }
    }

    private class Conjunction(targets: List<String>) : Node(targets) {
        val state: MutableMap<String, Boolean> = HashMap()

        fun setInputs(inputs: List<String>) {
            for (input in inputs) state[input] = false
        }

        override fun processPulse(source: String, high: Boolean): List<Pair<String, Boolean>> {
            state[source] = high
            return createPulses(!state.values.all { it })
        }
    }

    private fun parse(lines: List<String>): Map<String, Node> {
        val graph = HashMap<String, Node>()
        graph["output"] = Simple
        for (line in lines) {
            val (name, targets) = line.split(" -> ")
            val targetList = targets.split(", ")
            when {
                name == "broadcaster" -> graph[name] = Broadcaster(targetList)
                name.startsWith('%') -> graph[name.drop(1)] = FlipFlop(targetList)
                name.startsWith('&') -> graph[name.drop(1)] = Conjunction(targetList)
            }
        }
        for ((conName, conjunction) in graph.entries) if (conjunction is Conjunction)
            conjunction.setInputs(graph.entries.mapNotNull { (name, node) -> if (conName in node.targets) name else null })
        return graph
    }

    private fun simulateButtonPress(graph: Map<String, Node>): Pair<Int, Int> {
        val pulseQueue = ArrayDeque<Pulse>()
        pulseQueue.add(Pulse("button", "broadcaster", false))

        var lowPulses = 0
        var highPulses = 0

        while (pulseQueue.isNotEmpty()) {
            val (source, target, high) = pulseQueue.removeFirst()
            if (high) ++highPulses
            else ++lowPulses
            for ((newTarget, newHigh) in (graph[target] ?: Simple).processPulse(source, high))
                pulseQueue.addLast(Pulse(target, newTarget, newHigh))
        }

        return lowPulses to highPulses
    }

    override fun PrintStream.part1(lines: List<String>) {
        val graph = parse(lines)

        var totalLowPulses = 0L
        var totalHighPulses = 0L

        repeat(1000) {
            val (lowPulses, highPulses) = simulateButtonPress(graph)
            totalLowPulses += lowPulses
            totalHighPulses += highPulses
        }

        println(totalLowPulses * totalHighPulses)
    }

    override fun PrintStream.part2(lines: List<String>) {
        // Don't run on example
        if (lines.size < 10) return

        val output = Output()
        val graph = parse(lines) + ("rx" to output)
        var count = 0
        do {
            output.reset()
            ++count
            simulateButtonPress(graph)
            // Do for every sub-input and multiply all
            if (output.highPulses == 1) println(count)
        } while (true)
    }
}