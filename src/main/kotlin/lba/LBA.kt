package lba

data class LBA(
  val nStates: Int,
  val startState: Int,
  val finalStates: Set<Int>,
  val sigma: Set<Char>,
  val gamma: Set<Char>,
  val leftMarker: Char,
  val rightMarker: Char,
  val transitions: Set<Transition>
) {

  data class Transition(
    val from: Int,
    val to: Int,
    val by: Char,
    val writing: Char,
    val direction: MoveDirection
  ) {

    enum class MoveDirection {
      LEFT, RIGHT
    }
  }
}