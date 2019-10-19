package converter

import converter.Scheme.DslSymbol.TO
import grammar.Grammar
import lba.LBA
import lba.LBA.Transition.MoveDirection.LEFT
import lba.LBA.Transition.MoveDirection.RIGHT

object LBAtoGrammarConverter {

  fun convert(lba: LBA, includeSingleCellRules: Boolean = true): Grammar {
    val transformScheme = scheme {

      //<editor-fold desc="common">
      fun fstateSigmaGamma(init: (Int, Char, Char) -> Scheme.DslProduction) =
        states.filter { isFinalState(it) }.flatMap { q -> sigma.flatMap { a -> gammaWithoutMarkers.map { x -> init(q, a, x) } } }

      fun sigmaGamma(init: (Char, Char) -> Scheme.DslProduction) =
        sigma.flatMap { a -> gammaWithoutMarkers.map { x -> init(a, x) } }
      //</editor-fold>

      fun sigmaSigmaGamma(init: (Char, Char, Char) -> Scheme.DslProduction) =
        sigma.flatMap { a -> sigma.flatMap { b -> gammaWithoutMarkers.map { z -> init(a, b, z) } } }
      //</editor-fold>

      axiom { n("A1") }

      if (includeSingleCellRules) {

        //<editor-fold desc="1">
        commonRule { sigma.map { a -> production(n("A1"), TO, n("[$lm, $q0, $a, $a, $rm]")) } }
        //</editor-fold>

        //<editor-fold desc="2">
        transitionRule {
          predicate { isNotFinalState(q) && x == lm && y == lm && d == RIGHT }
          transform { sigmaGamma { a, z -> production(n("[$q, $lm, $z, $a, $rm]"), TO, n("[$lm, $p, $z, $a, $rm]")) } }
        }
        transitionRule {
          predicate { isNotFinalState(q) && x != lm && y != lm && d == LEFT }
          transform { sigma.map { a -> production(n("[$lm, $q, $x, $a, $rm]"), TO, n("[$p, $lm, $y, $a, $rm]")) } }
        }
        transitionRule {
          predicate { isNotFinalState(q) && x != lm && y != lm && d == RIGHT }
          transform { sigma.map { a -> production(n("[$lm, $q, $x, $a, $rm]"), TO, n("[$lm, $y, $a, $p, $rm]")) } }
        }
        transitionRule {
          predicate { isNotFinalState(q) && x == rm && y == rm && d == LEFT }
          transform { sigmaGamma { a, z -> production(n("[$lm, $z, $a, $q, $rm]"), TO, n("[$lm, $p, $z, $a, $rm]")) } }
        }
        //</editor-fold>

        //<editor-fold desc="3">
        commonRule { fstateSigmaGamma { q, a, x -> production(n("[$q, $lm, $x, $a, $rm]"), TO, t("$a")) } }
        commonRule { fstateSigmaGamma { q, a, x -> production(n("[$lm, $q, $x, $a, $rm]"), TO, t("$a")) } }
        commonRule { fstateSigmaGamma { q, a, x -> production(n("[$lm, $x, $a, $q, $rm]"), TO, t("$a")) } }
        //</editor-fold>
      }

      //<editor-fold desc="4">
      commonRule { sigma.map { a -> production(n("A1"), TO, n("[$lm, $q0, $a, $a]"), n("A2")) } }
      commonRule { sigma.map { a -> production(n("A2"), TO, n("[$a, $a]"), n("A2")) } }
      commonRule { sigma.map { a -> production(n("A2"), TO, n("[$a, $a, $rm]")) } }
      //</editor-fold>

      //<editor-fold desc="5">
      transitionRule {
        predicate { isNotFinalState(q) && x == lm && y == lm && d == RIGHT }
        transform { sigmaGamma { a, z -> production(n("[$q, $lm, $z, $a]"), TO, n("[$lm, $p, $z, $a]")) } }
      }
      transitionRule {
        predicate { isNotFinalState(q) && x != lm && y != lm && d == LEFT }
        transform { sigma.map { a -> production(n("[$lm, $q, $x, $a]"), TO, n("[$p, $lm, $y, $a]")) } }
      }
      transitionRule {
        predicate { isNotFinalState(q) && x != lm && y != lm  && d == RIGHT }
        transform {
          sigmaSigmaGamma { a, b, z ->
            production(n("[$lm, $q, $x, $a]"), n("[$z, $b]"), TO, n("[$lm, $y, $a]"), n("[$p, $z, $b]"))
          }
        }
      }
      transitionRule {
        predicate { isNotFinalState(q) && x != lm && y != lm  && d == RIGHT }
        transform {
          sigmaSigmaGamma { a, b, z ->
            production(n("[$lm, $q, $x, $a]"), n("[$z, $b, $rm]"), TO, n("[$lm, $y, $a]"), n("[$p, $z, $b, $rm]"))
          }
        }
      }
      //</editor-fold>

      //<editor-fold desc="6">
      transitionRule {
        predicate { isNotFinalState(q) && x != lm && y != lm  && d == RIGHT }
        transform {
          sigmaSigmaGamma { a, b, z ->
            production(n("[$q, $x, $a]"), n("[$z, $b]"), TO, n("[$y, $a]"), n("[$p, $z, $b]"))
          }
        }
      }
      transitionRule {
        predicate { isNotFinalState(q) && x != lm && y != lm  && d == LEFT }
        transform {
          sigmaSigmaGamma { a, b, z ->
            production(n("[$z, $b]"), n("[$q, $x, $a]"), TO, n("[$p, $z, $b]"), n("[$y, $a]"))
          }
        }
      }
      transitionRule {
        predicate { isNotFinalState(q) && x != lm && y != lm  && d == RIGHT }
        transform {
          sigmaSigmaGamma { a, b, z ->
            production(n("[$q, $x, $a]"), n("[$z, $b, $rm]"), TO, n("[$y, $a]"), n("[$p, $z, $b, $rm]"))
          }
        }
      }
      transitionRule {
        predicate { isNotFinalState(q) && x != lm && y != lm  && d == LEFT }
        transform {
          sigmaSigmaGamma { a, b, z ->
            production(n("[$lm, $z, $b]"), n("[$q, $x, $a]"), TO, n("[$lm, $p, $z, $b]"), n("[$y, $a]"))
          }
        }
      }
      //</editor-fold>

      //<editor-fold desc="7">
      transitionRule {
        predicate { isNotFinalState(q) && x == rm && y == rm && d == LEFT }
        transform { sigmaGamma { a, z -> production(n("[$z, $a, $q, $rm]"), TO, n("[$p, $z, $a, $rm]")) } }
      }
      transitionRule {
        predicate { isNotFinalState(q) && x != lm && y != lm  && d == RIGHT }
        transform { sigma.map { a -> production(n("[$q, $x, $a, $rm]"), TO, n("[$y, $a, $p, $rm]")) } }
      }
      transitionRule {
        predicate { isNotFinalState(q) && x != lm && y != lm  && d == LEFT }
        transform {
          sigmaSigmaGamma { a, b, z ->
            production(n("[$z, $b]"), n("[$q, $x, $a, $rm]"), TO, n("[$p, $z, $b]"), n("[$y, $a, $rm]"))
          }
        }
      }
      transitionRule {
        predicate { isNotFinalState(q) && x != lm && y != lm  && d == LEFT }
        transform {
          sigmaSigmaGamma { a, b, z ->
            production(n("[$lm, $z, $b]"), n("[$q, $x, $a, $rm]"), TO, n("[$lm, $p, $z, $b]"), n("[$y, $a, $rm]"))
          }
        }
      }
      //</editor-fold>

      //<editor-fold desc="8">
      commonRule { fstateSigmaGamma { q, a, x -> production(n("[$q, $lm, $x, $a]"), TO, t("$a")) } }
      commonRule { fstateSigmaGamma { q, a, x -> production(n("[$lm, $q, $x, $a]"), TO, t("$a")) } }
      commonRule { fstateSigmaGamma { q, a, x -> production(n("[$q, $x, $a]"), TO, t("$a")) } }
      commonRule { fstateSigmaGamma { q, a, x -> production(n("[$q, $x, $a, $rm]"), TO, t("$a")) } }
      commonRule { fstateSigmaGamma { q, a, x -> production(n("[$x, $a, $q, $rm]"), TO, t("$a")) } }
      //</editor-fold>

      //<editor-fold desc="9">
      commonRule { sigmaSigmaGamma { a, b, x -> production(t("$a"), n("[$x, $b]"), TO, t("$a"), t("$b")) } }
      commonRule { sigmaSigmaGamma { a, b, x -> production(t("$a"), n("[$x, $b, $rm]"), TO, t("$a"), t("$b")) } }
      commonRule { sigmaSigmaGamma { a, b, x -> production(n("[$x, $a]"), t("$b"), TO, t("$a"), t("$b")) } }
      commonRule { sigmaSigmaGamma { a, b, x -> production(n("[$lm, $x, $a]"), t("$b"), TO, t("$a"), t("$b")) } }
      //</editor-fold>
    }

    return transformScheme(lba)
  }
}
