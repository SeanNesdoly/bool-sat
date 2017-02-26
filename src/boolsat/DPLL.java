package boolsat;

/*
 * An implementation of the DPLL Algorithm for deciding the satisfiability of
 * propositional logic formulae in Conjunctive Normal Form.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 11th, 2017
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

public class DPLL {

    public static final String SAT = "The conclusion follows logically from the premises.\n";
    public static final String UNSAT = "The conclusion does not follow logically from the premises.\n";

    public boolean dpll_satisfiable(CNF F) {
        ArrayList<Literal> model = new ArrayList<Literal>();

        return dpll(F);
    }

    public static boolean dpll(CNF F) {
        System.out.println("running dpll on " + F);

        /*Set<Literal> lits = F.literalMap.keySet();
        Literal[] litsArr = lits.toArray(new Literal[0]);
        for (int j = 0; j < litsArr.length; j++) {
            List<Integer> cIndeces = F.literalMap.get(litsArr[j]);

            for (Iterator<Integer> i = cIndeces.iterator(); i.hasNext();) {
                System.out.println("" + litsArr[j] + ": " + i.next());
            }
        }*/

        // if every clause in F is true, return true
        boolean satisfiable = true;
        for(Iterator<Clause> i = F.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            if (!c.isClauseTrue()) {
                System.out.println(c + " is false");
                satisfiable = false; // found a false clause
                break;
            }
        }

        if (satisfiable) { // all clauses in F are true
            System.out.println(F + " is satisfiable");
            return true;
        }

        System.out.println("checking for empty clause");
        if (F.containsEmptyClause()) // some clause is false in the model
            return false;

        System.out.println("eliminating pure literals, F before = " + F);
        F.eliminate_pure_literals();

        System.out.println("F after = " + F);

        System.out.println("handling unit clauses");
        /*ArrayList<Literal> unit_clauses = F.find_all_unit_clauses();
        for (int i = 0; i < unit_clauses.size(); i++) {
            F.unit_propagate(unit_clauses.get(i));
        }*/

        // simplify formula by handling unit clauses
        Clause unit_clause = F.find_unit_clause();
        while (unit_clause != null) {
            F.unit_propagate(unit_clause.toLiteral());
            unit_clause.literals.get(0).setLiteralTrue();
            System.out.println("unit_propagate: " + F);

            unit_clause = F.find_unit_clause();
        }
        System.out.println("after unit propagation: " + F);

        Literal l = F.choose_literal();
        System.out.println("literal chosen: " + l);
        Literal negatedL = l.createNegatedLiteral();

        CNF F1 = new CNF(F);
        CNF F2 = new CNF(F);

        if (l != null) {
            F1.setAllLiteralInstancesTrue(l);
            F2.setAllLiteralInstancesTrue(negatedL);
        }

        System.out.println("assigning literal values & recursive calls");
        return dpll(F1) || dpll(F2);
    }

 }
