package LBA;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public final class LBA {
  @Range(from = 1, to = Integer.MAX_VALUE) private final int nStates;
  @Range(from = 1, to = Integer.MAX_VALUE) private final int startState;
  @NotNull private final Set<Integer> finalStates;
  @NotNull private final Set<Character> sigma;
  @NotNull private final Set<Character> gamma;
  @NotNull private final Set<Transition> transitions;
  
  public LBA(
      int nStates,
      int startState,
      @NotNull Set<Integer> finalStates,
      @NotNull Set<Character> sigma,
      @NotNull Set<Character> gamma,
      @NotNull Set<Transition> transitions
  ) {
    this.nStates = nStates;
    this.startState = startState;
    this.finalStates = new HashSet<>(finalStates);
    this.sigma = new HashSet<>(sigma);
    this.gamma = new HashSet<>(gamma);
    this.transitions = new TreeSet<>(transitions);
  }
  
  public static class Transition implements Comparable<Transition> {
    private final int from;
    private final int to;
    private final char by;
    private final char writing;
    @NotNull private final MoveDirection direction;
    
    public Transition(
        int from,
        int to,
        char by,
        char writing,
        @NotNull MoveDirection direction
    ) {
      this.from = from;
      this.to = to;
      this.by = by;
      this.writing = writing;
      this.direction = direction;
    }
    
    public int getFrom() {
      return from;
    }
    
    public int getTo() {
      return to;
    }
    
    public char getBy() {
      return by;
    }
    
    public char getWriting() {
      return writing;
    }
    
    @NotNull
    public MoveDirection getDirection() {
      return direction;
    }
    
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      
      Transition that = (Transition) o;
      
      if (from != that.from) {
        return false;
      }
      if (to != that.to) {
        return false;
      }
      if (by != that.by) {
        return false;
      }
      if (writing != that.writing) {
        return false;
      }
      return direction == that.direction;
    }
    
    @Override
    public int hashCode() {
      int result = from;
      result = 31 * result + to;
      result = 31 * result + (int) by;
      result = 31 * result + (int) writing;
      result = 31 * result + direction.hashCode();
      return result;
    }
    
    @Override
    public int compareTo(@NotNull Transition o) {
      if (from == o.getFrom()) {
        return Integer.compare(to, o.getTo());
      } else {
        return Integer.compare(from, o.getFrom());
      }
    }
    
    public enum MoveDirection {
      LEFT, RIGHT
    }
  }
}