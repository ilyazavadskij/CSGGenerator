package grammar

import java.util.*

data class Grammar(
  val terminals: Set<String>,
  val nonTerminals: Set<String>,
  val axiom: String,
  val productions: Set<Production>
) {

  fun findFirst(n: Int, trackLengthRestriction: Int = Int.MAX_VALUE, consumer: (String) -> Unit) {
    val queue = ArrayDeque<List<String>>()
    queue.addLast(listOf(axiom))

    val visited = mutableSetOf<List<String>>()

    var found = 0
    while (found < n && queue.isNotEmpty()) {
      val head = queue.pollFirst()

      if (head.size > trackLengthRestriction)
        continue

      if (head !in visited) visited += head
      else continue

      if (head.all { it in terminals }) {
        found++
        consumer(head.joinToString(separator = ""))
      } else {
        queue.addAll(products(head))
      }
    }
  }

  fun minimize(): Grammar = removeUnreachable().removeUngenerative()

  fun removeUnreachable(): Grammar {
    val reachableNonTerminals = allReachableFrom(axiom)

    return copy(
      nonTerminals = reachableNonTerminals,
      productions = productions.filterTo(mutableSetOf()) { p -> p.from.all { it in reachableNonTerminals } }
    )
  }

  fun removeUngenerative(): Grammar {
    val generativeNonTerminals =
      nonTerminals.filterTo(mutableSetOf()) { nt -> allReachableFrom(nt, strict = false).any { it in terminals } }

    return copy(
      nonTerminals = generativeNonTerminals,
      productions = productions.filterTo(mutableSetOf()) { p -> p.to.all { it in generativeNonTerminals } }
    )
  }

  private fun products(symbols: List<String>): Set<List<String>> =
    productions.mapNotNullTo(mutableSetOf()) { production ->
      val ind = symbols.indexOfSublist(production.from)
      when (ind) {
        -1   -> null
        else -> symbols.subList(0, ind) + production.to + symbols.subList(ind + production.from.size, symbols.size)
      }
    }

  private fun <T> List<T>.indexOfSublist(sublist: List<T>): Int =
    (0..size - sublist.size).indexOfFirst { subList(it, it + sublist.size) == sublist }

  private fun allReachableFrom(nonTerminal: String, strict: Boolean = true): Set<String> {
    fun iter(reachable: Set<String>): Set<String> {
      val next = mutableSetOf<String>().apply { addAll(reachable) }
      productions.forEach { prod ->
        when (strict) {
          true  -> if (prod.from.all { it in reachable }) next.addAll(prod.to)
          false -> if (prod.from.any { it in reachable }) next.addAll(prod.to)
        }
      }
      return next
    }

    return generateSequence(setOf(nonTerminal), ::iter)
      .zipWithNext()
      .first { (s1, s2) -> s1 == s2 }
      .first
  }

  data class Production(val from: List<String>, val to: List<String>)
}