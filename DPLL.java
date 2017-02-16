/*
 * An implementation of the DPLL Algorithm for deciding the satisfiability of
 * propositional logic formulae in conjuctive normal form.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 11th, 2017
 */

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

public class DPLL {

    private static final String SAT = "The conclusion follows logically from the premises.\n";
    private static final String UNSAT = "The conclusion does not follow logically from the premises.\n";

    public boolean dpll(Set<Literal> literals, Set<Clause> clauses) {


        return false;
    }



    // look for all non-unit clauses that contain the literal l and remove them,
    // as there exists an assignment of the literal that makes all clauses containing l
    // true; additionally, remove from all clauses any instance of the negated literal !l
    private static CNF unit_propogate(Literal l, CNF formula) {
        Literal negated_l = l.createNegatedLiteral();

        for(Iterator<Clause> i = formula.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            // remove all non-unit clauses containing the literal l
            if (!c.isUnitClause() && c.literals.contains(l)) {
                i.remove();
                System.out.println("removing clause: " + c);
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

        return formula;
    }



    private void pure_literal_elimination(CNF formula) {

    }



    // Wrapper class for a propositional logic literal (A | !A).
    // A positive literal is represented internally as a positive int (+x); a
    // negavtive literal is represented internally as a negative int (-x)
    private class Literal {
        String s; // symbol of literal
        boolean sign; // sign of literal; true=a positive literal, false=a negative literal
        boolean val; // the value actually assigned to the symbol (!A where A has value true is false)
        boolean isAssigned; // flag for this literal being assigned in the DPLL algorithm

        // parse out a Literal from a String
        public Literal(String litStr) {
            if (litStr != null && litStr.length() == 0)
                System.err.println("Invalid literal instantiation.");
            else if (litStr.charAt(0) == '!') {
                sign = false; // negative literal
                litStr = litStr.substring(1,litStr.length());
            } else
                sign = true; // positive literal

            s = litStr;
            isAssigned = false;
        }

        // creates a literal with the given symbol and sign (sign=false=!A)
        public Literal(String symbol, boolean _sign) {
            this.s = symbol;
            this.sign = _sign;

            isAssigned = false;
        }

        // equivalency is defined on the following attributes: symbol s, sign l
        @Override public boolean equals(Object other) {
            if (this == other) return true;

            if (!(other instanceof Literal)) return false;

            Literal otherLit = (Literal)other;

            // TODO: may require comparison of val/isAssigned
            return (s.equals(otherLit.s) && sign == otherLit.sign);
        }

        @Override public int hashCode() {
            int result = 7; // prime number

            // compute hash value
            result = 37*result + s.hashCode();
            result = 37*result + (sign?1:0);

            return result;
        }

        // compute the value of the literal with consideration of its sign
        public boolean computeValue() {
            if (sign) // positive literal
                return val;
            else // negative literal
                return !val;
        }

        // negate the literal by flipping the boolean value of the sign
        public void negate() {
            sign = !sign;
        }

        // creates an instance of this literal that is negated
        public Literal createNegatedLiteral() {
            return new Literal(this.s, !this.sign);
        }

        // convenience method to print out a literal
        public String toString() {
            return (this.sign ? "" : "!") + s;
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
        Set<Literal> allLiterals; // may contain !A and A
        LinkedList<Clause> clauses;
        String formula; // CNF formula in clausal form

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

                    // update current index in CNF clausal form sentence
                    index = endBracket + 1;
                } else if (formula.charAt(index) == ',') {
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

                    index = endClause + 1;
                }
            }
        }

        // find the first instance of a unit clause in the formula; note that a unit clause
        // is simply a clause that contains only one literal (!A or A)
        public ArrayList<Clause> find_all_unit_clauses() {
            ArrayList<Clause> unit_clauses = new ArrayList<Clause>();

            for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
                Clause c = i.next();

                if (c.isUnitClause())
                    unit_clauses.add(c);
            }

            return unit_clauses;
        }

        public void eliminate_pure_literals() {
            for (Literal l: this.allLiterals) {
                // if we find an instance of the negated literal, l is NOT pure
                Literal negated_l = l.createNegatedLiteral();
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

            /*for (Literal lit:cnf.allLiterals)
                System.out.println("~" + lit);*/


            ArrayList<Clause> all_unit_clauses = cnf.find_all_unit_clauses();
            for (Clause unit_clause : all_unit_clauses) {
                cnf = unit_propogate(unit_clause.toLiteral(), cnf);
                System.out.println(cnf);
            }


            /*while (c != null) {
                System.out.println("unit_clause: " + c);
                cnf = unit_propogate(c.toLiteral(), cnf);

                c = cnf.find_all_unit_clauses();
            }

            cnf = unit_propogate(c.toLiteral(), cnf);
            System.out.println(cnf);

            //c = cnf.find_unit_clause();

            cnf = unit_propogate(c.toLiteral(), cnf);
            System.out.println(cnf);*/


            TextFile.writeFile(SAT);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return;
        }

    }
 }
