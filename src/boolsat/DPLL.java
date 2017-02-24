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

public class DPLL {

    public static final String SAT = "The conclusion follows logically from the premises.\n";
    public static final String UNSAT = "The conclusion does not follow logically from the premises.\n";

    public boolean dpll_satisfiable(CNF F) {
        ArrayList<Literal> model = new ArrayList<Literal>();

        return dpll(F);
    }

    public boolean dpll(CNF F) {
        System.out.println("running dpll");
        // if every clause in F is true, return true
        boolean satisfiable = true;
        for(Iterator<Clause> i = F.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            if (!c.value) {
                satisfiable = false; // found a false clause
                break;
            }
        }

        if (satisfiable) // all clauses in F are true
            return true;

        System.out.println("checking for empty clause");
        if (F.containsEmptyClause()) // some clause is false in the model
            return false;

        System.out.println("eliminating pure literals");
        F.eliminate_pure_literals();

        System.out.println(F);

        System.out.println("handling unit clauses");
        ArrayList<Literal> unit_clauses = F.find_all_unit_clauses();
        for (int i = 0; i < unit_clauses.size(); i++) {
            F.unit_propagate(unit_clauses.get(i));
        }
        // simplify formula by handling unit clauses
        /*Literal unit_clause = F.find_unit_clause();
        while (unit_clause != null) {
            F.unit_propagate(unit_clause);

            unit_clause = F.find_unit_clause();
        }*/

        System.out.println("choosing a literal");
        Literal l = F.choose_literal();
        Literal negatedL = l.createNegatedLiteral();

        CNF F1 = new CNF(F);
        CNF F2 = new CNF(F);

        System.out.println("assigning literal values");
        if (l != null) {
            F1.setAllLiteralInstancesTrue(l);
            F2.setAllLiteralInstancesTrue(negatedL);
        }

        System.out.println("recursive calls");
        return dpll(F1) || dpll(F2);
    }



/*
    public static void main(String[] args) {

        ArrayList<String> formulas = null;
        try {
            formulas = TextFile.readFile();
            //System.out.println(formulas.get(0));

            // TODO: parse formulas into clausal CNF from Mary

            CNF cnf = new DPLL().new CNF(formulas.get(0));
            System.out.println(cnf.formula);
            System.out.println(cnf);

            //for (Literal lit:cnf.allLiterals)
            //    System.out.println("~" + lit);


            ArrayList<Clause> all_unit_clauses = cnf.find_all_unit_clauses();
            for (Clause unit_clause : all_unit_clauses) {
                cnf = unit_propagate(unit_clause.toLiteral(), cnf);
                System.out.println(cnf);
            }


            while (c != null) {
                System.out.println("unit_clause: " + c);
                cnf = unit_propagate(c.toLiteral(), cnf);

                c = cnf.find_all_unit_clauses();
            }

            cnf = unit_propagate(c.toLiteral(), cnf);
            System.out.println(cnf);

            //c = cnf.find_unit_clause();

            cnf = unit_propagate(c.toLiteral(), cnf);
            System.out.println(cnf);


            TextFile.writeFile(SAT);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return;
        }

    }*/

 }
