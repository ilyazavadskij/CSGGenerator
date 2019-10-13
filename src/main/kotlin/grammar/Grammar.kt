package grammar

data class Grammar(
  val terminals: Set<String>,
  val nonTerminals: Set<String>,
  val axiom: String,
  val productions: Set<Production>
) {

  data class Production(val from: List<String>, val to: List<String>)
}
