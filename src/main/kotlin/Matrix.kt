typealias Matrix<T> = List<List<T>>
typealias MutableMatrix<T> = MutableList<MutableList<T>>
typealias Point = Pair<Int, Int>

fun <T> List<List<T>>.find(elem: T): Pair<Int, Int> {
    for (i in indices) {
        for (j in this[i].indices) {
            if (this[i][j] == elem) return i to j
        }
    }
    throw NoSuchElementException()
}

operator fun <T> List<List<T>>.get(point: Pair<Int, Int>) = this[point.first][point.second]

operator fun <T> MutableList<MutableList<T>>.set(point: Pair<Int, Int>, value: T) {
    this[point.first][point.second] = value
}