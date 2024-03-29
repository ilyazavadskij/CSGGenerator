import com.google.gson.Gson
import converter.LBAtoGrammarConverter
import grammar.Grammar
import lba.LBA
import java.io.FileReader
import java.io.FileWriter


fun main() {
  val gson = Gson()

  val lba = FileReader("LBA.json").use { gson.fromJson(it, LBA::class.java) }
  val grammar = LBAtoGrammarConverter.convert(lba, includeSingleCellRules = false)
  val minimized = grammar.minimize()
  FileWriter("CSG.json").use { it.write(gson.toJson(minimized, Grammar::class.java)) }

  println("Raw       production amount: ${grammar.productions.size}")
  println("Minimized production amount: ${minimized.productions.size}")

  minimized.findFirst(10, trackLengthRestriction = 30) { System.out.printf("%2d: %s\n", it.length, it) }

  val unrestrictedGrammar = LBAtoUnrestrictedGrammarConverter(lba).convert()
  val minimizedUnrestrictedGrammar = unrestrictedGrammar.minimize()

  unrestrictedGrammar.productions.stream().forEach(System.out::println)
  FileWriter("UG.json").use { it.write(gson.toJson(minimizedUnrestrictedGrammar, Grammar::class.java)) }

  println("Raw       production amount: ${unrestrictedGrammar.productions.size}")
  println("Minimized production amount: ${minimizedUnrestrictedGrammar.productions.size}")

  minimizedUnrestrictedGrammar.findFirst(10, trackLengthRestriction = 30) { System.out.printf("%2d: %s\n", it.length, it) }
}