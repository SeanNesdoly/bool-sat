package boolsat;

/*
 * A wrapper class for the disjunction of a set of Literals.
 * A clause with only one Literal is termed a "unit clause".
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 24th, 2017
 */

import java.util.LinkedList;

public class Clause {

    LinkedList<Literal> literals; // the set of disjuncted literals in the clause
    public boolean value; // the truth value of the clause

    // constructor for creating a clause from an array of literals
    public Clause(String[] literals) {
        this.literals = new LinkedList<Literal>();
        for (String l : literals) {
            this.literals.add(new Literal(l));
        }

        value = false; // default
    }

    // constructor for creating a unit clause from a literal
    public Clause(Literal l) {
        literals = new LinkedList<Literal>();
        literals.add(l);

        value = false; // default
    }

    // constructor for creating a unit clause from a string
    public Clause(String l) {
        this(new Literal(l));
    }

    // if a literal has been set true in this clause, then the entire clause is true
    public void setClauseTrue() {
        this.value = true;
    }

    // determines if this clause is a unit clause (only 1 literal)
    public boolean isUnitClause() {
        return literals.size() == 1;
    }

    // determines if this clause has no literals
    // TODO: set of unit clauses {Q,!Q} gives an empty clause on either model
    public boolean isEmptyClause() {
        return literals.size() == 0;
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
        if (this.value)
            return "true";

        if (this.isUnitClause())
            return this.toLiteral().toString();

        String out = "(";
        for (Literal l : literals) {
            out += l.toString() + ",";
        }

        return out.substring(0, out.length()-1) + ")";
    }
}
