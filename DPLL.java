/*
 * An implementation of the DPLL Algorithm for deciding the satisfiability of
 * propositional logic formulae in conjuctive normal form.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 11th, 2017
 */

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;

public class DPLL {

    private static final String SAT = "The conclusion follows logically from the premises.\n";
    private static final String UNSAT = "The conclusion does not follow logically from the premises.\n";

    public boolean dpll(Set<Literal> literals, Set<Clause> clauses) {


        return false;
    }



    // find the first instance of a unit clause in the formula; note that a unit clause
    // is simply a clause that contains only one literal
    private static Clause find_unit_clause(CNF formula) {
        for(Iterator<Clause> i = formula.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            if (c.isUnitClause())
                return c;
        }

        return null;
    }



    // look for all non-unit clauses that contain the unit clause l and remove them,
    // as there exists an assignment of the unit clause that makes all clauses with that literal
    // true; additionally, remove from all clauses any instance of the negated unit clause
    private static CNF unit_propogate(Literal l, CNF formula) {
        Literal negated_l = l.createNegatedLiteral();

        for(Iterator<Clause> i = formula.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            // remove all non-unit clauses that contain the unit clause l
            if (!c.isUnitClause() && c.literals.indexOf(l) != -1) {
                System.out.println("removed");
                i.remove();
                continue;
            }

            // remove all instances of the negated unit clause for each clause in the formula
            while (c.literals.indexOf(negated_l) != -1) {
                // we are not modifying the actual iterator here so it is a safe operation!
                c.literals.remove(negated_l);
            }
        }

        return formula;
    }



    // TODO: has been replaced by unit_propogate.. will remove
    private void propogate_units(CNF formula) {
        for(Iterator<Clause> iterator1 = formula.clauses.iterator(); iterator1.hasNext();) {
            Clause c = iterator1.next();

            if (c.isUnitClause()) {
                //Literal l = c.literals.toArray(new Literal[1])[0]; // grab the only literal in the clause

                // look for all non-unit clauses that contain the single literal x in c and remove them,
                // as there exists an assignment of the unit clause c that makes all clauses with x true
                for(Iterator<Clause> iterator2 = formula.clauses.iterator(); iterator2.hasNext();) {
                    Clause other = iterator2.next();

                    // remove all non-unit clauses that contain the literal unit cluase c
                    /*if (!other.isUnitClause() && other.contains(c)) {
                        iterator2.remove(); //formula.clauses.remove(other);
                    }*/

                    // remove all instances of the negated literal c in every clause

                }
            }
        }
    }

    private void pure_literal_elimination(CNF formula) {

    }



    // Wrapper class for a propositional logic literal (A | !A).
    // A positive literal is represented internally as a positive int (+x); a
    // negavtive literal is represented internally as a negative int (-x)
    private class Literal {
        char s; // symbol of literal
        int l; // +l = a positive literal; -l = a negative literal
        boolean val; // the value assigned to this literal
        boolean isAssigned; // flag for this literal being assigned in the DPLL algorithm

        // Parse out a Literal from a String!
        public Literal(String litStr) {
            if (litStr.length() == 0)
                System.err.println("Invalid literal instantiation.");
            else if (litStr.length() == 1) { // positive literal A
                s = litStr.charAt(0);
                l = (int)s;
            } else { // negative literal !A
                s = litStr.charAt(1);
                l = -s;
            }

            isAssigned = false;
        }

        public Literal(char symbol, boolean sign) {
            s = symbol;
            if (sign)
                l = (int)symbol; // positive literal
            else
                l = -symbol; // negative literal

            isAssigned = false; // maybe not?
        }

        // compute the value of the literal with consideration of its sign
        public boolean computeValue() {
            if (l>0) // positive literal
                return val;
            else // negative literal
                return !val;
        }

        // negate the literal by flipping the sign of the integer representation
        public void negate() {
            l = -l;
        }

        public Literal createNegatedLiteral() {
            return new Literal(this.s, this.l > 0 ? false:true);
        }

        // convenience method to print out a literal
        public String toString() {
            return (this.l > 0 ? "":"!") + s;
        }
    }



    // Wrapper class for the disjunction of a set of Literals; the only exception
    // is a unit clause, which is only 1 literal!
    private class Clause {

        LinkedList<Literal> literals;

        public Clause(String[] literals) {
            this.literals = new LinkedList<Literal>();
            for (String l : literals) {
                this.literals.add(new Literal(l));
            }
        }

        public Clause(String l) {
            literals = new LinkedList<Literal>();
            Literal unitClause = new Literal(l);
            literals.add(unitClause);
        }

        // determines if this clause is a unit clause (only 1 literal)
        public boolean isUnitClause() {
            return literals.size() == 1;
        }

        // converts a unit clause to a single literal
        public Literal toLiteral() {
            if (this.isUnitClause()) {
                return literals.get(0);
            }

            return null;
        }

        // convenience method to print out a clause in clausal form
        public String toString() {
            if (this.isUnitClause())
                return this.toLiteral().toString();

            String out = "(";
            for (Literal l : literals) {
                out += l.toString() + ",";
            }

            return out.substring(0, out.length()-1) + ")";
        }
    }



    // Wrapper class for a propositional logic formula in CNF
    public class CNF {
        Set<Literal> literals;
        LinkedList<Clause> clauses;
        String formula; // CNF formula in clausal form

        public CNF(String _cnf) {
            clauses = new LinkedList<Clause>();
            formula = _cnf;

            // TODO: parse CNF string into Clauses & Literals

            formula = formula.substring(1, formula.length() - 1); // trim off curly braces

            int index = 0;
            while (index < formula.length()) {
                if (formula.charAt(index) == '(') {
                    int endBracket = formula.indexOf(')', index);
                    String[] literals = formula.substring(index+1, endBracket).split(",");

                    clauses.add(new Clause(literals));

                    // update current index in CNF clausal form sentence
                    index = endBracket + 1;
                } else if (formula.charAt(index) == ',') {
                    index++;
                } else { // unit clause (a single literal without brackets)
                    if (formula.charAt(index) == '!') {
                        // parse !A
                        clauses.add(new Clause(formula.substring(index, index+2)));
                        index += 2;
                    } else {
                        // parse A
                        clauses.add(new Clause("" + formula.charAt(index)));
                        index++;
                    }
                }
            }
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



    public static void main(String[] args) {

        ArrayList<String> formulas = null;
        try {
            formulas = TextFile.readFile();
            //System.out.println(formulas.get(0));

            // TODO: parse formulas into CNF in clausal form from Mary

            CNF cnf = new DPLL().new CNF(formulas.get(0));
            System.out.println(cnf.formula);
            System.out.println(cnf);

            Clause c = find_unit_clause(cnf);
            System.out.println(c);

            cnf = unit_propogate(c.toLiteral(), cnf);
            System.out.println(cnf);

            TextFile.writeFile(SAT);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return;
        }

    }
 }
