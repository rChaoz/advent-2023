inline fun <T> List<T>.split(predicate: (T) -> Boolean) = buildList<List<T>> {
    var list = mutableListOf<T>()
    for (item in this@split) {
        if (predicate(item)) {
            add(list)
            list = mutableListOf()
        } else list.add(item)
    }
    add(list)
}