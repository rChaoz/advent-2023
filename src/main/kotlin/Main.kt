import kotlin.system.exitProcess

private const val dayCount = 1
var verbose: Boolean = false
var output: Boolean = false

fun main(args: Array<String>) {
    val day = if (args.isNotEmpty()) args[0] else "all"
    if (day == "help") usage(null, null)

    val part = if (args.size >= 2) args[1] else "all"
    verbose = args.size >= 3

    if (part !in listOf("example1", "example2", "1", "2", "all")) usage("part", part)

    if (day == "all") repeat(dayCount) { runDay(it + 1, part) }
    output = verbose
    runDay(day.toIntOrNull().takeIf { it in 1..dayCount } ?: usage("day", day), part)
}

fun usage(paramName: String?, paramValue: String?): Nothing {
    if (paramName != null && paramValue != null) System.err.println("Invalid value for $paramName: $paramValue")
    println("""
        Usage:  <launch command> [day] [part] [verbose]
        ${'\t'}day     - 'all' or a number between 1 and $dayCount (inclusive)
        ${'\t'}part    - which part (half) of the puzzle day to run, can be 'example1', 'example2', '1', '2' or 'all'
        ${'\t'}verbose - whether to be verbose (any value means yes, use 2 parameters or less for no)
    """.trimIndent())
    exitProcess(1)
}

fun runDay(num: Int, part: String) {
    val day = {}.javaClass.classLoader.loadClass("aoc.Day${num}").getDeclaredConstructor().newInstance() as Day
    if (part == "all") for (what in listOf("example1", "example2", "1", "2")) day.run(what)
    else day.run(part)
}