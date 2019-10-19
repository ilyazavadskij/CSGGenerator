package converter

import grammar.Grammar
import grammar.Grammar.Production
import lba.LBA
import lba.LBA.Transition

internal fun scheme(init: Scheme.() -> Unit): (LBA) -> Grammar = { lba ->
  val buildScheme = Scheme(lba)
  buildScheme.init()
  buildScheme.build()
}

internal class Scheme(val lba: LBA) {
  private var axiom: DslSymbol.NonTerminal? = null
  private val commonRules = mutableListOf<() -> Collection<DslProduction>>()
  private val transitionRules = mutableListOf<Transition.() -> Collection<DslProduction>>()

  fun axiom(init: () -> DslSymbol.NonTerminal) {
    require(axiom == null) { "Axiom was specified more than once" }
    axiom = init()
  }

  fun commonRule(init: () -> Collection<DslProduction>) {
    commonRules.add(init)
  }

  fun transitionRule(init: TransitionTransformation.() -> Unit) {
    val tt = TransitionTransformation()
    tt.init()
    transitionRules.add { tt.apply(this) }
  }

  fun production(vararg symbols: DslSymbol): DslProduction {
    val indSep = symbols.indexOf(DslSymbol.TO)
    val ss = symbols.map { it.s }
    val production = Production(ss.subList(0, indSep), ss.subList(indSep + 1, ss.size))
    val nonTerminals = symbols.filterIsInstance(DslSymbol.NonTerminal::class.java).mapTo(mutableSetOf()) { it.s }
    val terminals = symbols.filterIsInstance(DslSymbol.Terminal::class.java).mapTo(mutableSetOf()) { it.s }

    return DslProduction(production, nonTerminals, terminals)
  }

  class DslProduction(val production: Production, val nonTerminals: Set<String>, val terminals: Set<String>)

  sealed class DslSymbol(open val s: String) {
    data class NonTerminal internal constructor(override val s: String) : DslSymbol(s)
    data class Terminal internal constructor(override val s: String) : DslSymbol(s)
    object TO : DslSymbol("<separator>")
  }

  fun n(s: String): DslSymbol.NonTerminal = DslSymbol.NonTerminal(s)
  fun t(s: String): DslSymbol.Terminal = DslSymbol.Terminal(s)

  fun isFinalState(st: Int) = lba.finalStates.contains(st)
  fun isNotFinalState(st: Int) = !isFinalState(st)

  inline val sigma: Set<Char> get() = lba.sigma
  inline val gamma: Set<Char> get() = lba.gamma
  inline val gammaWithoutMarkers: Set<Char> get() = lba.gamma - lba.leftMarker - lba.rightMarker
  inline val states: IntRange get() = 1..lba.nStates
  inline val q0: Int get() = lba.startState
  inline val lm: Char get() = lba.leftMarker
  inline val rm: Char get() = lba.rightMarker

  internal fun build(): Grammar {
    val dslProductions = mutableSetOf<DslProduction>()
    commonRules.flatMapTo(dslProductions) { it() }
    transitionRules.flatMapTo(dslProductions) { lba.transitions.flatMap(it) }

    val productions = dslProductions.mapTo(mutableSetOf()) { it.production }
    val nonTerminals = dslProductions.flatMapTo(mutableSetOf()) { it.nonTerminals }
    val terminals = dslProductions.flatMapTo(mutableSetOf()) { it.terminals }

    requireNotNull(axiom) { "Axiom was not specified" }
    return Grammar(terminals, nonTerminals, axiom!!.s, productions)
  }
}

internal class TransitionTransformation {
  private var predicate: (Transition.() -> Boolean)? = null
  private var transform: (Transition.() -> Collection<Scheme.DslProduction>)? = null

  fun predicate(init: Transition.() -> Boolean) {
    require(predicate == null) { "Predicate was specified more than once" }
    predicate = init
  }

  fun transform(init: Transition.() -> Collection<Scheme.DslProduction>) {
    require(transform == null) { "Transform was specified more than once" }
    transform = init
  }

  inline val Transition.q: Int get() = from
  inline val Transition.p: Int get() = to
  inline val Transition.x: Char get() = by
  inline val Transition.y: Char get() = writing
  inline val Transition.d: Transition.MoveDirection get() = direction

  internal fun apply(transition: Transition): Collection<Scheme.DslProduction> {
    requireNotNull(transform) { "Transform was not specified" }
    return transition.takeIf(predicate ?: { true })?.run(transform!!) ?: emptySet()
  }
}