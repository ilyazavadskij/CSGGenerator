import grammar.Grammar;
import lba.LBA;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class LBAtoUnrestrictedGrammarConverter {

    private LBA lba;

    public LBAtoUnrestrictedGrammarConverter(LBA lba) {
        this.lba = lba;
    }

    public LBA getLba() {
        return lba;
    }

    public void setLba(LBA lba) {
        this.lba = lba;
    }


    public Grammar convert() {
        List<String> left = new ArrayList<>();
        List<String> right = new ArrayList<>();

        Set<String> terminals = lba.getGamma().stream().map(Object::toString).collect(Collectors.toSet());
        terminals.add(String.valueOf(' '));
        Set<String> nonTerminals = new HashSet<>();
        nonTerminals.add("X");
        nonTerminals.add("Y");
        nonTerminals.add("Z");
        String axiom = "X";
        Set<Grammar.Production> productions = new HashSet<>();

//        Правила:
//        1. X→q0Y
        left.add("X");
        right.add(String.valueOf(lba.getStartState()));
        right.add("Y");
        productions.add(new Grammar.Production(left, right));
//        System.out.println("1. " + productions.size());

//        2. Y→[a,a]Y для каждого a∈Σ
        for (Character a : lba.getSigma()) {
            left = new ArrayList<>();
            right = new ArrayList<>();
            left.add("Y");
            right.add("[" + a + ", " + a + "]");
            right.add("Y");
            productions.add(new Grammar.Production(left, right));
        }
//        System.out.println("2. " + productions.size());

//        3. Y→Z
        left = new ArrayList<>();
        right = new ArrayList<>();
        left.add("Y");
        right.add("Z");
        productions.add(new Grammar.Production(left, right));
//        System.out.println("3. " + productions.size());


//        4. Z→[e,B]Z
        left = new ArrayList<>();
        right = new ArrayList<>();
        left.add("Z");
        right.add("[" + ' ' + ", " + "B" + "]");
        right.add("Z");
        productions.add(new Grammar.Production(left, right));
//        System.out.println("4. " + productions.size());

//        5. Z→e
        left = new ArrayList<>();
        right = new ArrayList<>();
        left.add("Z");
        right.add(String.valueOf(' '));
        productions.add(new Grammar.Production(left, right));
//        System.out.println("5. " + productions.size());
//
        Set<Character> sigma_epsilon = lba.getSigma();
        sigma_epsilon.add(' ');

//        6. q[a,C]→[a,E]p для каждого a∈Σ∪{e} и каждого q∈Q и C∈Γ такого, что D(q,C)=(p,E,R)
        for (int q = 1; q <= lba.getNStates(); q++) {
            for (Character C : lba.getGamma()) {
                for (int p = 1; p <= lba.getNStates(); p++) {
                    for (Character E : lba.getGamma()) {
                        for (Character a : sigma_epsilon) {
                            LBA.Transition transition = new LBA.Transition(q, p, C, E, LBA.Transition.MoveDirection.RIGHT);
                            if (lba.getTransitions().contains(transition)) {
                                left = new ArrayList<>();
                                right = new ArrayList<>();
                                left.add(String.valueOf(q));
                                left.add("[" + a + ", " + C + "]");
                                right.add("[" + a + ", " + E + "]");
                                right.add(String.valueOf(p));
                                productions.add(new Grammar.Production(left, right));
                            }
                        }
                    }
                }
            }
        }
//        System.out.println("6. " + productions.size());

//        7. [b,I]q[a,C]→p[b,I][a,J] для каждого C,J,I из Γ, a и b и p,q∈Q таких, что D(q,C)=(p,J,L)
        for (int q = 1; q <= lba.getNStates(); q++) {
            for (Character C : lba.getGamma()) {
                for (int p = 1; p <= lba.getNStates(); p++) {
                    for (Character J : lba.getGamma()) {
                        for (Character I : lba.getGamma()) {
                            for (Character a : sigma_epsilon) {
                                for (Character b : sigma_epsilon) {
                                    LBA.Transition transition = new LBA.Transition(q, p, C, J, LBA.Transition.MoveDirection.LEFT);
                                    if (lba.getTransitions().contains(transition)) {
                                        left = new ArrayList<>();
                                        right = new ArrayList<>();
                                        left.add("[" + b + ", " + I + "]");
                                        left.add(String.valueOf(q));
                                        left.add("[" + a + ", " + C + "]");
                                        right.add(String.valueOf(p));
                                        right.add("[" + b + ", " + I + "]");
                                        right.add("[" + a + ", " + J + "]");
                                        if (q == 1) {
                                            System.out.println(transition);
                                        }
                                        productions.add(new Grammar.Production(left, right));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//        System.out.println("7. " + productions.size());

//        8. [a,C]q→qaq для каждого a∈Σ∪{e}, C∈Γ, q∈F
        for (Character a : sigma_epsilon) {
            for (Character C : lba.getGamma()) {
                for (Integer q : lba.getFinalStates()) {
                    left = new ArrayList<>();
                    right = new ArrayList<>();
                    left.add("[" + a + ", " + C + "]");
                    left.add(String.valueOf(q));
                    right.add(String.valueOf(q));
                    right.add(String.valueOf(a));
                    right.add(String.valueOf(q));
                    productions.add(new Grammar.Production(left, right));
                }
            }
        }
//        System.out.println("8. " + productions.size());

//        9. q[a,C]→qaq для каждого a∈Σ∪{e}, C∈Γ, q∈F
        for (Character a : sigma_epsilon) {
            for (Character C : lba.getGamma()) {
                for (Integer q : lba.getFinalStates()) {
                    left = new ArrayList<>();
                    right = new ArrayList<>();
                    left.add(String.valueOf(q));
                    left.add("[" + a + ", " + C + "]");
                    right.add(String.valueOf(q));
                    right.add(String.valueOf(a));
                    right.add(String.valueOf(q));
                    productions.add(new Grammar.Production(left, right));
                }
            }
        }
//        System.out.println("9. " + productions.size());

//        10. q→e для каждого q∈F
        for (Integer q : lba.getFinalStates()) {
            left = new ArrayList<>();
            right = new ArrayList<>();
            left.add(String.valueOf(q));
            right.add(String.valueOf(' '));
            productions.add(new Grammar.Production(left, right));
        }
//        System.out.println("10. " + productions.size());

        //productions.forEach(System.out::println);
        return new Grammar(terminals, nonTerminals, axiom, productions);
        //Files.write(Paths.get("./transitions"), stringBuilder.toString().getBytes());
    }
}
