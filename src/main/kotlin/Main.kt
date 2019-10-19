import com.google.gson.Gson
import converter.LBAtoGrammarConverter
import grammar.Grammar
import lba.LBA
import java.io.FileReader
import java.io.FileWriter
import java.util.*

fun main() {
  val gson = Gson()

  val lba = FileReader("LBA2.json").use { gson.fromJson(it, LBA::class.java) }
  val grammar = LBAtoGrammarConverter.convert(lba, includeSingleCellRules = false)
  FileWriter("CSG.json").use { it.write(gson.toJson(grammar, Grammar::class.java)) }

  val minimized = minimize(grammar)

  println("Total     production amount: ${grammar.productions.size}")
  println("Minimized production amount: ${minimized.productions.size}")

  grammar.findFirst(1, ::println)
}

fun Grammar.findFirst(n: Int, consumer: (String) -> Unit) {
  val queue = ArrayDeque<List<String>>()
  queue.addLast(listOf(axiom))

  var found = 0
  while (found < n) {
    val head = queue.pollFirst()

    println(head)

    if (head.all { it in terminals }) {
      found++
      consumer(head.joinToString(separator = ""))
    } else {
      queue.addAll(products(head))
    }
  }
}

fun Grammar.products(symbols: List<String>): Set<List<String>> =
  productions.mapNotNullTo(mutableSetOf()) { production ->
    val ind = symbols.indexOfSublist(production.from)
    when (ind) {
      -1 -> null
      else -> symbols.subList(0, ind) + production.to + symbols.subList(ind + 1, symbols.size)
    }
  }

fun <T> List<T>.indexOfSublist(sublist: List<T>): Int =
  (0..size - sublist.size).indexOfFirst { subList(it, it + sublist.size) == sublist }

fun minimize(grammar: Grammar): Grammar {
  fun iter(reachable: Set<String>): Set<String> {
    val next = mutableSetOf<String>().apply { addAll(reachable) }
    grammar.productions.forEach { prod ->
      if (reachable.containsAll(prod.from))
        next.addAll(prod.to)
    }
    return next
  }

  val reachableNonTerminals =
    generateSequence(setOf(grammar.axiom), ::iter)
      .zipWithNext()
      .takeWhile { (s1, s2) -> s1 != s2 }
      .last()
      .first

  return grammar.copy(
    nonTerminals = reachableNonTerminals,
    productions = grammar.productions.filterTo(mutableSetOf()) { p -> p.from.all { it in reachableNonTerminals } }
  )
}
