package boolsat;

/*
 * Wrapper class for a propositional logic formula in Conjunctive Normal Form.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 24th, 2017
 */

import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;

public class CNF {

    String formula; // CNF formula in clausal form
    Set<Literal> allLiterals; // the set of all unique literals in the CNF formula
    LinkedList<Clause> clauses; // the set of clauses in the CNF formula

    public CNF(String _cnf) {
        allLiterals = new HashSet<Literal>();
        clauses = new LinkedList<Clause>();
        formula = _cnf;

        formula = formula.substring(1, formula.length() - 1); // trim off curly braces

        int index = 0;
        while (index < formula.length()) {
            if (formula.charAt(index) == '(') {
                int endBracket = formula.indexOf(')', index); // guaranteed a matching end bracket
                String[] strLiterals = formula.substring(index+1, endBracket).split(",");

                // add a new clause from the set of literals
                Clause c = new Clause(strLiterals);
                clauses.add(c);

                // add all unique literals to the set of literals in this CNF formula
                for (Literal lit:c.literals)
                    allLiterals.add(lit);

                index = endBracket + 1; // update current index in the CNF formula
            } else if (formula.charAt(index) == ',') { // skip over commas between clauses
                index++;
            } else { // parse unit clause (a single literal without brackets)
                int endClause = formula.indexOf(',', index);
                if (endClause == -1) { // case where we are at the last clause in the formula
                    endClause = formula.length();
                }

                // create a new unit clause & add it to the set of all literals for this formula
                Clause c = new Clause(formula.substring(index, endClause));
                clauses.add(c);
                allLiterals.add(c.literals.get(0));

                index = endClause + 1; // update current index in the CNF formula
            }
        }
    }


    // copy constructor
    public CNF(CNF F) {
        this.formula = F.formula;
        this.allLiterals = new HashSet<Literal>(F.allLiterals);
        this.clauses = new LinkedList<Clause>(F.clauses);
    }


    // look for all non-unit clauses that contain the literal l and remove them,
    // as there exists an assignment of the literal that makes all clauses containing l
    // true; additionally, remove from all clauses any instance of the negated literal !l
    public void unit_propagate(Literal l) {
        Literal negated_l = l.createNegatedLiteral();

        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            // remove all non-unit clauses containing the literal l
            if (!c.isUnitClause() && c.literals.contains(l)) {
                System.out.println("removing clause: " + c);
                //i.remove();
                c.setClauseTrue();
                continue;
            }

            // in every clause that contains the negated literal !l, delete it
            while (c.literals.contains(negated_l)) {
                // we are not modifying the actual iterator here so it is a safe operation!
                c.literals.remove(negated_l);

                // if we have removed all literals from the clause, delete the clause
                // TODO: is the above correct? an empty clause implies unsatisfiability..
                if (c.literals.size() == 0) {
                    i.remove();
                }
                System.out.println("removing literal:" + negated_l);
            }
        }
    }


    // find the first instance of a unit clause in the formula; note that a unit clause
    // is simply a clause that contains only one literal (!A or A)
    public ArrayList<Literal> find_all_unit_clauses() {
        ArrayList<Literal> unit_clauses = new ArrayList<Literal>();

        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            if (c.isUnitClause())
                unit_clauses.add(c.toLiteral());
        }

        return unit_clauses;
    }

    // if it exists, find the next instance of a unit clause in the CNF formula
    public Literal find_unit_clause() {
        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            Literal l = c.toLiteral(); // returns null if the clause is not a literal
            if (l != null)
                return l;
        }

        return null;
    }

    // eliminate all pure literals found in the formula
    // TODO: currently O(n^2) algorithm... do not look at literals in TRUE clauses
    public void eliminate_pure_literals() {
        for (Literal l : this.allLiterals) {
            // if we find an instance of the negated literal, l is NOT pure
            Literal negated_l = l.createNegatedLiteral();
            boolean pure = !this.allLiterals.contains(negated_l);

            // remove all instances of the pure literal and add in the unit clause l
            if (pure) {
                for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
                    Clause c = i.next();

                    c.literals.remove(l);

                    // remove the clause if we have removed all literals
                    if (c.isEmptyClause())
                        i.remove();
                }

                // add in the single pure literal unit clause
                this.clauses.add(new Clause(l));
            }
        }
    }

    // selects the next literal in the set of literals contained in this CNF formula
    // TODO: apply heuristic here?
    public Literal choose_literal() {
        Literal[] arrayOfLiterals = this.allLiterals.toArray(new Literal[0]);
        return (arrayOfLiterals.length != 0 ? arrayOfLiterals[0] : null);
    }

    // set all instances of the literal l to "true"
    public void setAllLiteralInstancesTrue(Literal l) {
        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            if (c.literals.contains(l)) {
                int j = c.literals.indexOf(l);
                c.literals.get(j).setLiteralTrue();
            }
        }
    }

    // returns whether or not this CNF formula contains an empty clause
    public boolean containsEmptyClause() {
        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();
            if (c.isEmptyClause())
                return true;
        }

        return false; // we did not find an empty clause
    }

    // convenience method to print out a CNF formula in clausal form
    public String toString() {
        String out = "{";
        for (Clause c : clauses) {
            out += c.toString() + ",";
        }

        return out.substring(0, out.length()-1) + "}";
    }
}
