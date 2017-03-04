package boolsat;

/*
 * An implementation of the DPLL Algorithm for deciding the satisfiability of
 * propositional logic formulae in Conjunctive Normal Form.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 11th, 2017
 */

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public class DPLL {

    public static final String SAT = "The conclusion follows logically from the premises.\n";
    public static final String UNSAT = "The conclusion does not follow logically from the premises.\n";

    public static List<Literal> assignments = new LinkedList<Literal>();;
    public static boolean solutionFound = false;

    public static boolean dpll(CNF F) {
        System.out.println("\nrunning dpll on: " + F);

        /*-------------------
         * UNIT PROPAGATION
         *-------------------*/
        System.out.println("handling unit clauses");
        // simplify formula by handling unit clauses
        Clause unit_clause = F.find_unit_clause();
        while (unit_clause != null && !F.containsEmptyClause()) {
            System.out.println("\tcurrent unit clause: " + unit_clause);
            F.unit_propagate(unit_clause.toLiteral());
            System.out.println("\tapplied unit propagation: " + F);
            unit_clause = F.find_unit_clause();
        }
        System.out.println("after unit_propogation: " + F);


        /*---------------------------
         * CHECK FOR AN EMPTY CLAUSE
         *---------------------------*/
        if (F.containsEmptyClause())
            return false;


        /*-------------------------
         * CHECK FOR TRUE FORMULA
         *-------------------------*/
        // the formula is true if every clause in F is true
        boolean satisfiable = true;
        for(Iterator<Clause> i = F.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            if (!c.isClauseTrue()) {
                System.out.println("false clause: " + c);
                satisfiable = false; // found a false clause
                break;
            }
        }

        // all clauses in F are true
        if (satisfiable) {
            System.out.println(F + " is satisfiable");

            // collect all literal assignments of the satisfied formula
            Set<Literal> uniqueLiterals = new HashSet<Literal>(F.allLiterals);
            uniqueLiterals.addAll(F.branchLiterals);

            for (Literal l : uniqueLiterals) {
                if (l.isAssigned) {
                    // only keep the first solution found
                    if (!solutionFound)
                        assignments.add(l);

                    // UNCOMMENT the following to produce all solutions!
                    //System.out.println("\t" + l + "=" + l.computeValue() + "\t\tval=" + l.val + "\tbAssigned=" + l.isAssigned);
                }
            }

            solutionFound = true;
            return true;
        }


        /*---------------------
         * CHOOSING A LITERAL
         *---------------------*/
        Literal l = F.choose_literal();
        if (l == null) {
            System.out.println("no literals left to choose");
            return false;
        }
        System.out.println("literal chosen: " + l);

        CNF F1 = new CNF(F);
        CNF F2 = new CNF(F);


        /*------------
         * BRANCHING
         *------------*/
        F1.setAllLiteralInstancesTrue(l);
        System.out.print("BRANCH 1: " + F1 + "\n\tcurrent branch literals: ");
        for (Literal bL : F1.branchLiterals) {
            System.out.print(bL + " ");
        }
        System.out.println("");

        Literal negated_l = l.createNegatedLiteral();
        F2.setAllLiteralInstancesTrue(negated_l);
        System.out.print("BRANCH 2: " + F2 + "\n\tcurrent branch literals: ");
        for (Literal bL : F2.branchLiterals) {
            System.out.print(bL + " ");
        }
        System.out.println("");

        boolean branch1 = dpll(F1);
        boolean branch2 = dpll(F2);
        return branch1 || branch2;
    }

 }
