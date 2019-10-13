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
  FileWriter("CSG.json").use { it.write(gson.toJson(grammar, Grammar::class.java)) }
}
