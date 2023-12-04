import java.io.OutputStream
import java.io.PrintStream
import java.lang.RuntimeException
import java.nio.file.Files.createDirectories
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.outputStream
import kotlin.io.path.readLines

abstract class Day {
    protected abstract fun PrintStream.part1(lines: List<String>)
    protected abstract fun PrintStream.part2(lines: List<String>)

    private val dayNumber by lazy { this::class.simpleName!!.removePrefix("Day").toInt() }

    fun run(what: String) {
        val day = dayNumber.toString()
        val inputPath = Path("input", day, if (what.startsWith("example")) "$what.txt" else "input.txt")
        if (!inputPath.exists()) {
            System.err.println("Warning: file not found - $inputPath, skipping")
            return
        }
        val outputPath = Path("output", day, "$what.txt")
        createDirectories(outputPath.parent)
        val lines = inputPath.readLines().let { if (it.isNotEmpty() && it.last().isEmpty()) it.subList(0, it.size - 2) else it }

        fun PrintStream.go() {
            when (what) {
                "example1" -> part1(lines)
                "example2" -> part2(lines)
                "1" -> part1(lines)
                "2" -> part2(lines)
                else -> throw RuntimeException("Unknown action to run: $what")
            }
        }

        val stream = outputPath.outputStream()

        if (verbose) {
            println("Day\t$dayNumber - $what:")
            PrintStream(object : OutputStream() {
                override fun write(b: Int) {
                    System.out.write(b)
                    stream.write(b)
                }
            }).go()
            println()
        } else PrintStream(stream).go()
    }
}