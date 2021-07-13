package initiate.common.utils

import kotlin.math.max
import kotlin.math.min

object MathUtils {
    private fun levenshtein(
        s: String, t: String,
        charScore: (Char, Char) -> Int = { c1, c2 -> if (c1 == c2) 0 else 1 }
    ): Int {

        if (s == t) return 0
        if (s == "") return t.length
        if (t == "") return s.length

        val initialRow: List<Int> = (0 until t.length + 1).map { it }.toList()
        return (s.indices).fold(initialRow, { previous, u ->
            (t.indices).fold(mutableListOf(u + 1), { row, v ->
                row.add(
                    minOf(
                        row.last() + 1,
                        previous[v + 1] + 1,
                        previous[v] + charScore(s[u], t[v])
                    )
                )
                row
            })
        }).last()
    }

    fun levenshteinRatio(s: String, t: String): Double {
        return (s.length + t.length - levenshtein(s, t).toDouble()) / (s.length + t.length)
    }

    private const val deleteCost: Int = 1
    private const val insertCost: Int = 1
    private const val replaceCost: Int = 1
    private const val swapCost: Int = 1

    private fun damerauLevenshtein(source: String, target: String): Int {
        if (source.isEmpty()) {
            return target.length * insertCost
        }
        if (target.isEmpty()) {
            return source.length * deleteCost
        }
        val table = Array(source.length) { IntArray(target.length) }
        val sourceIndexByCharacter = HashMap<Char, Int>()
        if (source[0] != target[0]) {
            table[0][0] = min(replaceCost, deleteCost + insertCost)
        }
        sourceIndexByCharacter[source[0]] = 0
        for (i in 1 until source.length) {
            val deleteDistance = table[i - 1][0] + deleteCost
            val insertDistance = (i + 1) * deleteCost + insertCost
            val matchDistance = i * deleteCost + if (source[i] == target[0]) 0 else replaceCost
            table[i][0] = intArrayOf(deleteDistance, insertDistance, matchDistance).minOrNull()!!
        }
        for (j in 1 until target.length) {
            val deleteDistance = table[0][j - 1] + insertCost
            val insertDistance = (j + 1) * insertCost + deleteCost
            val matchDistance = j * insertCost + if (source[0] == target[j]) 0 else replaceCost
            table[0][j] = intArrayOf(deleteDistance, insertDistance, matchDistance).minOrNull()!!
        }
        for (i in 1 until source.length) {
            var maxSourceLetterMatchIndex = if (source[i] == target[0]) 0 else -1
            for (j in 1 until target.length) {
                val candidateSwapIndex: Int? = sourceIndexByCharacter[target[j]]
                val jSwap = maxSourceLetterMatchIndex
                val deleteDistance = table[i - 1][j] + deleteCost
                val insertDistance = table[i][j - 1] + insertCost
                var matchDistance = table[i - 1][j - 1]
                if (source[i] != target[j]) {
                    matchDistance += replaceCost
                } else {
                    maxSourceLetterMatchIndex = j
                }
                var swapDistance = Integer.MAX_VALUE;
                if (candidateSwapIndex != null && jSwap != -1) {
                    swapDistance = 0
                    if (candidateSwapIndex > 0 || jSwap > 0) {
                        swapDistance = table[max(0, candidateSwapIndex - 1)][max(0, jSwap - 1)]
                    }
                    swapDistance += (i - candidateSwapIndex - 1) * deleteCost
                    swapDistance += (j - jSwap - 1) * insertCost + swapCost
                }
                table[i][j] = intArrayOf(deleteDistance, insertDistance, matchDistance, swapDistance).minOrNull()!!
            }
            sourceIndexByCharacter[source[i]] = i
        }
        return table[source.length - 1][target.length - 1]
    }

    fun damerauLevenshteinRatio(sRaw: String?, tRaw: String?): Double {
        val (s, t) = Pair(sRaw.orEmpty(), tRaw.orEmpty())
        val distance = (s.length + t.length - damerauLevenshtein(s, t).toDouble()) / (s.length + t.length)
        return if (!distance.isNaN()) distance else 0.0
    }
}
