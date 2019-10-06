public class Main {
  public static void main(String[] args) {
  
  }
}

/*
*
* final int nStates = 20;
    final int startState = 1;
    final Set<Integer> finalStates = new HashSet<Integer>() {{
      add(17);
    }};
    
    final Set<Character> sigma = new HashSet<Character>() {{
      add('1');
    }};
    final Set<Character> gamma = new HashSet<Character>() {{
      add('$'); // left marker
      add('^'); // right marker
      addAll(sigma);
      add('B');
      add('#');
    }};
    
    final Set<Transition> transitions = new TreeSet<Transition>() {{
      add(new Transition(1, 2, '^', '^', LEFT));
      add(new Transition(1, 3, '1', '1', RIGHT));
      add(new Transition(3, 2, '^', '^', LEFT));
      add(new Transition(3, 4, '1', 'B', LEFT));
      add(new Transition(4, 4, '1', '1', LEFT));
      add(new Transition(4, 5, 'B', 'B', RIGHT));
      add(new Transition(4, 5, '#', '#', RIGHT));
      add(new Transition(5, 6, 'B', 'B', RIGHT));
      add(new Transition(6, 6, '#', '#', RIGHT));
      add(new Transition(6, 14, '^', '^', LEFT));
      add(new Transition(6, 7, '1', 'B', RIGHT));
      add(new Transition(7, 2, '^', '^', LEFT));
      add(new Transition(7, 8, '1', '1', LEFT));
      add(new Transition(8, 9, 'B', 'B', LEFT));
      add(new Transition(9, 9, '#', '1', LEFT));
      add(new Transition(9, 10, 'B', 'B', RIGHT));
      add(new Transition(10, 10, '1', '1', RIGHT));
      add(new Transition(10, 4, 'B', 'B', LEFT));
      add(new Transition(5, 11, '1', '#', RIGHT));
      add(new Transition(11, 11, '1', '1', RIGHT));
      add(new Transition(11, 12, 'B', 'B', RIGHT));
      add(new Transition(12, 12, '#', '#', RIGHT));
      add(new Transition(12, 14, '^', '^', LEFT));
      add(new Transition(12, 13, '1', '#', LEFT));
      add(new Transition(13, 13, '#', '#', LEFT));
      add(new Transition(13, 4, 'B', 'B', LEFT));
      add(new Transition(14, 14, '#', '1', LEFT));
      add(new Transition(14, 14, 'B', 'B', LEFT));
      add(new Transition(14, 15, '$', '$', RIGHT));
      add(new Transition(15, 15, '1', '1', RIGHT));
      add(new Transition(15, 16, 'B', '1', RIGHT));
      add(new Transition(16, 17, '^', '^', LEFT));
      add(new Transition(16, 18, '1', 'B', RIGHT));
      add(new Transition(18, 17, '^', '^', LEFT));
      add(new Transition(18, 19, '1', '1', RIGHT));
      add(new Transition(18, 19, 'B', '1', RIGHT));
      add(new Transition(19, 19, '1', '1', RIGHT));
      add(new Transition(19, 19, 'B', '1', RIGHT));
      add(new Transition(19, 20, '^', '^', LEFT));
      add(new Transition(20, 20, '1', '1', LEFT));
      add(new Transition(20, 4, 'B', 'B', LEFT));
    }};
    
    LBA lba = new LBA(nStates, startState, finalStates, sigma, gamma, transitions);
    
    Gson serializer = new Gson();
    
    String s = serializer.toJson(lba);
    
    System.out.println(s);
*
*
* */
