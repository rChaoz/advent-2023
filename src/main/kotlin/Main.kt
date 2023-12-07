import kotlin.system.exitProcess

private val days = buildList {
    var i = 1
    while (true) {
        try {
            add({}.javaClass.classLoader.loadClass("aoc.Day$i"))
        } catch (e: ClassNotFoundException) {
            break
        }
        ++i
    }
}
var verbose: Boolean = false
var output: Boolean = false

fun main(args: Array<String>) {
    val day = if (args.isNotEmpty()) args[0] else "all"
    if (day == "help") usage(null, null)

    val part = if (args.size >= 2) args[1] else "all"
    verbose = args.size >= 3

    if (part !in listOf("example1", "example2", "1", "2", "all")) usage("part", part)

    if (day == "all") repeat(days.size) { runDay(it + 1, part) }
    output = verbose
    runDay(day.toIntOrNull().takeIf { it in 1..days.size } ?: usage("day", day), part)
}

fun usage(paramName: String?, paramValue: String?): Nothing {
    if (paramName != null && paramValue != null) System.err.println("Invalid value for $paramName: $paramValue")
    println("""
        Usage:  <launch command> [day] [part] [verbose]
        ${'\t'}day     - 'all' or a number between 1 and ${days.size} (inclusive)
        ${'\t'}part    - which part (half) of the puzzle day to run, can be 'example1', 'example2', '1', '2' or 'all'
        ${'\t'}verbose - whether to be verbose (any value means yes, use 2 parameters or less for no)
    """.trimIndent())
    exitProcess(1)
}

fun runDay(num: Int, part: String) {
    val day = days[num - 1].getDeclaredConstructor().newInstance() as Day
    if (part == "all") for (what in listOf("example1", "1", "example2", "2")) day.run(what)
    else day.run(part)
}