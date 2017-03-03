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
import java.util.Iterator;

public class Clause {

    LinkedList<Literal> literals; // the set of disjuncted literals in the clause

    // constructor for creating a clause from an array of string literals
    public Clause(String[] literals) {
        this.literals = new LinkedList<Literal>();
        for (String s : literals) {
            this.literals.add(new Literal(s));
        }
    }

    // constructor for creating a unit clause from a single string literal
    public Clause(String l) {
        this(new Literal(l));
    }

    // constructor for creating a unit clause from a literal
    public Clause(Literal l) {
        literals = new LinkedList<Literal>();
        literals.add(l);
    }

    // if at least 1 literal is true in the disjunction of literals, then the entire clause is true
    public boolean isClauseTrue() {
        for (Literal l : literals) {
            if (l.computeValue())
                return true;
        }

        return false; // no true literal found
    }

    // Determines if this is a unit clause or not. Note that a unit clause may be a single literal,
    // or, where 1 literal is not assigned and the rest are assigned false
    public boolean isUnitClause() {
        if (literals.size() == 1)
            return true;

        // case: one literal is not assigned & the rest are assigned false
        int trueCount = 0;
        for (Iterator<Literal> i = this.literals.iterator(); i.hasNext();) {
            Literal l = i.next();

            if (l.computeValue())
                trueCount++;
        }

        if (trueCount != 1)
            return false;

        return false;
    }

    // determines if this clause has no literals
    // TODO: set of unit clauses {Q,!Q} gives an empty clause on either model interpretation
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
        if (this.isUnitClause())
            return this.toLiteral().toString() + "=" + isClauseTrue();

        if (this.isEmptyClause()) {
            return "EMPTY";
        }

        String out = "(";
        for (Literal l : literals) {
            out += l.toString() + "=" + l.val + ",";
        }

        return out.substring(0, out.length()-1) + ")";
    }
}
