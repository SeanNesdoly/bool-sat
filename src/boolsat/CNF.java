package boolsat;

/*
 * Wrapper class for a propositional logic formula in Conjunctive Normal Form.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 24th, 2017
 */

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;

public class CNF {

    String formula; // CNF formula in clausal form
    List<Literal> allLiterals; // the set of all literals in the formula
    List<Literal> branchLiterals; // store branching literals on recursive descent
    List<Clause> clauses; // the set of clauses in the CNF formula

    public CNF(String _cnf) {
        allLiterals = new LinkedList<Literal>();
        branchLiterals = new LinkedList<Literal>();
        clauses = new LinkedList<Clause>();
        formula = _cnf;

        formula = formula.substring(1, formula.length() - 1); // trim off curly braces

        int index = 0; // keep track of current position in the formula string
        int clauseIndex = 0; // keep track of the number of clauses
        while (index < formula.length()) {
            if (formula.charAt(index) == '(') {
                int endBracket = formula.indexOf(')', index); // guaranteed a matching end bracket
                String[] strLiterals = formula.substring(index+1, endBracket).split(",");

                // add a new clause from the set of literals
                Clause c = new Clause(strLiterals);
                clauses.add(c);

                // add all literals to the set of literals in this formula
                for (Literal l : c.literals) {
                    allLiterals.add(l);
                }

                index = endBracket + 1; // update current index in the CNF formula
                clauseIndex++;
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

                allLiterals.add(c.toLiteral());

                index = endClause + 1; // update current index in the CNF formula
                clauseIndex++;
            }
        }
    }


    // copy constructor
    public CNF(CNF F) {
        this.formula = F.formula;

        // copy literals
        List<Literal> newLiterals = new LinkedList<Literal>();
        for (Literal l : F.allLiterals) {
            newLiterals.add(new Literal(l));
        }
        this.allLiterals = newLiterals;

        // copy branch literals
        this.branchLiterals = new LinkedList<Literal>();
        List<Literal> copyBranchLits = new LinkedList<Literal>();
        for (Literal l : F.branchLiterals) {
            copyBranchLits.add(new Literal(l));
        }
        this.branchLiterals = copyBranchLits;

        // copy clauses
        this.clauses = new LinkedList<Clause>();
        List<Clause> newClauses = new LinkedList<Clause>();
        for (Clause c : F.clauses) {
            newClauses.add(new Clause(c));
        }
        this.clauses = newClauses;
    }


    // look for all non-unit clauses that contain the literal l and remove them,
    // as there exists an assignment of the literal that makes all clauses containing l
    // true; additionally, remove from all clauses any instance of the negated literal !l
    public void unit_propagate(Literal l) {
        // set all literals to true
        for (Literal other : allLiterals) {
            if (l.equals(other))
                other.setLiteralTrue();
        }
        l.setLiteralTrue();
        branchLiterals.add(l);

        Literal negated_l = l.createNegatedLiteral();

        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            // remove all non-unit clauses containing the literal l
            if (!c.isUnitClause() && c.literals.contains(l)) {
                System.out.println("\tremoving clause: " + c);
                for (Literal cLit : c.literals)
                    allLiterals.remove(cLit);

                i.remove();
                continue;
            }

            // in every clause that contains the negated literal !l, delete it
            while (c.literals.contains(negated_l)) {
                // we are not modifying the actual iterator here so it is a safe operation!
                c.literals.remove(negated_l);
                allLiterals.remove(negated_l);
                System.out.println("\tremoving literal: " + negated_l);
            }
        }
    }

    // Find all unit clause instances in the formula.
    // note that a unit clause may be a clause with one literal (!A or A), or,
    // a clause with one unassigned literal & the rest assigned as false
    public ArrayList<Literal> find_all_unit_clauses() {
        ArrayList<Literal> unit_clauses = new ArrayList<Literal>();

        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            if (c.isUnitClause())
                unit_clauses.add(c.toLiteral());
        }

        return unit_clauses;
    }

    // If it exists, find the next instance of a unit clause in the CNF formula.
    // note that a unit clause may be a clause with one literal (!A or A), or,
    // a clause with one unassigned literal & the rest assigned as false
    public Clause find_unit_clause() {
        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            if (c.isUnitClause() && !c.isClauseTrue())
                return c;
        }

        return null;
    }

    // eliminate all pure literals found in the formula & replace them with TRUE unit clauses
    public void eliminate_pure_literals() {
        for (Literal l : allLiterals) {

            if (l.computeValue()) // do not look at satisfied literals
                continue;

            // l is NOT pure if we find an instance of the negated literal
            Literal negated_l = l.createNegatedLiteral();
            boolean pure = !allLiterals.contains(negated_l);

            // remove all instances of the pure literal & add it back in as a true unit clause
            if (pure) {
                for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
                    Clause c = i.next();

                    if (c.literals.contains(l)) {
                        c.literals.remove(l);
                        allLiterals.remove(l);
                    }
                }

                l.setLiteralTrue();
                branchLiterals.add(l);
            }
        }
    }

    // Selects the next literal in the set of literals contained in this CNF formula
    // that has not yet been assigned a value
    public Literal choose_literal() {
        for (Iterator<Literal> i = allLiterals.iterator(); i.hasNext();) {
            Literal l = i.next();

            if (!l.isAssigned) {
                //branchLiterals.add(l);
                return l;
            }
        }

        return null;
    }

    // Set all instances of the literal l to "true" in the CNF formula.
    // note: this means all literals l AND !l are assigned a value of true, without
    // consideration of the sign. Thus, !l will evaluate to false, and l will evaluate to true
    public void setAllLiteralInstancesTrue(Literal l) {
        System.out.println("setting all instances true: " + l);
        // set all literals to true
        for (Literal other : allLiterals) {
            if (l.equals(other)) {
                other.setLiteralTrue();
            }
        }
        l.setLiteralTrue();
        branchLiterals.add(l);

        Literal negated_l = l.createNegatedLiteral();

        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            if (!c.isUnitClause() && c.literals.contains(l)) {
                for (Literal cLit : c.literals) {
                    allLiterals.remove(cLit);
                }

                System.out.println("\tremoving clause: " + c);
                i.remove();
                continue;
            }

            // in every clause that contains the negated literal !l, delete it
            while (c.literals.contains(negated_l)) {
                c.literals.remove(negated_l);
                allLiterals.remove(negated_l);
                System.out.println("\tremoving literal: " + negated_l);
            }
        }
    }

    // Set all instances of the literal l to "true" in the CNF formula.
    // note: this means all literals l AND !l are assigned a value of true, without
    // consideration of the sign. Thus, !l will evaluate to false, and l will evaluate to true
    public void setAllLiteralInstancesTrue2(Literal l) {
        String symbol = l.s;
        for (Iterator<Literal> i = allLiterals.iterator(); i.hasNext();) {
            Literal other = i.next();

            if (symbol.equals(other.s)) {
                other.val = true;
                other.isAssigned = true;
            }
        }


    }

    // Set all instances of the literal l to "false" in the CNF formula.
    // note: this means all literals l AND !l are assigned a value of false, without
    // consideration of the sign. Thus, !l will evaluate to true, and l will evaluate to false
    public void setAllLiteralInstancesFalse2(Literal l) {
        String symbol = l.s;
        for (Iterator<Literal> i = allLiterals.iterator(); i.hasNext();) {
            Literal other = i.next();

            if (symbol.equals(other.s)) {
                other.val = false;
                other.isAssigned = true;
            }
        }
    }

    // Set all instances of the literal l to "true" in the CNF formula.
    public void setAllLiteralInstancesTrue3(Literal l) {
        for (Iterator<Literal> i = allLiterals.iterator(); i.hasNext();) {
            Literal other = i.next();

            if (l.equals(other)) {
                other.setLiteralTrue();
            }
        }
    }

    // Returns whether or not this CNF formula contains an empty clause
    public boolean containsEmptyClause() {
        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();
            if (c.isEmptyClause()) {
                System.out.println("empty clause found: " + c);
                return true;
            }
        }

        return false; // no empty clause found
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
