package boolsat;

/*
 * Wrapper class for a propositional logic formula in Conjunctive Normal Form.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 24th, 2017
 */

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;

public class CNF {

    String formula; // CNF formula in clausal form
    Map<Literal, List<Integer>> literalMap; // the set of all literals in the formula mapped to clauses
    List<Clause> clauses; // the set of clauses in the CNF formula

    public CNF(String _cnf) {
        literalMap = new HashMap<Literal, List<Integer>>();
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

                // add all literals to the set of literals in this formula, mapped to this clause
                for (Literal lit : c.literals) {
                    if (literalMap.containsKey(lit)) {
                        literalMap.get(lit).add(clauseIndex);
                    } else {
                        List<Integer> clauseList = new LinkedList<Integer>();
                        clauseList.add(clauseIndex);
                        literalMap.put(lit, clauseList);
                    }
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

                if (literalMap.containsKey(c.toLiteral())) {
                    literalMap.get(c.toLiteral()).add(clauseIndex);
                } else {
                    List<Integer> clauseList = new LinkedList<Integer>();
                    clauseList.add(clauseIndex);
                    literalMap.put(c.literals.get(0), clauseList);
                }

                index = endClause + 1; // update current index in the CNF formula
                clauseIndex++;
            }
        }
    }


    // copy constructor
    public CNF(CNF F) {
        this.formula = F.formula;
        this.literalMap = new HashMap<Literal, List<Integer>>(F.literalMap);
        this.clauses = new LinkedList<Clause>(F.clauses);
    }


    // look for all non-unit clauses that contain the literal l and remove them,
    // as there exists an assignment of the literal that makes all clauses containing l
    // true; additionally, remove from all clauses any instance of the negated literal !l
    public void unit_propagate2(Literal l) {
        l.setLiteralTrue();
        Literal negated_l = l.createNegatedLiteral();

        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            // remove all non-unit clauses containing the literal l
            if (!c.isUnitClause() && c.literals.contains(l)) {
                System.out.println("setting clause true: " + c);
                //i.remove();
                //c.setClauseTrue();
                continue;
            }

            // in every clause that contains the negated literal !l, delete it
            while (c.literals.contains(negated_l)) {
                // we are not modifying the actual iterator here so it is a safe operation!
                c.literals.remove(negated_l);
                System.out.println("removing literal:" + negated_l);

                // if we have removed all literals from the clause, delete the clause
                // TODO: is the above correct? an empty clause implies unsatisfiability..
                if (c.literals.size() == 0) {
                    i.remove();
                    System.out.println("removing clause: " + c);
                }
            }
        }
    }


    // look for all non-unit clauses that contain the literal l and remove them,
    // as there exists an assignment of the literal that makes all clauses containing l
    // true; additionally, remove from all clauses any instance of the negated literal !l
    public void unit_propagate(Literal l) {
        l.setLiteralTrue();

        // remove all non-unit clauses containing the literal l
        for (Iterator<Integer> i = this.literalMap.get(l).iterator(); i.hasNext();) {
            int cIndex = i.next();
            Clause c = clauses.get(cIndex);

            if (!c.isUnitClause() && c.literals.contains(l)) {
                System.out.println("removing clause: " + c);
                removeClause(c);
            }
        }

        // in every non-unit clause that contains the negated literal "!l", delete it
        Literal negated_l = l.createNegatedLiteral();
        if (this.literalMap.containsKey(negated_l)) {

            for (Iterator<Integer> i = this.literalMap.get(negated_l).iterator(); i.hasNext();) {
                int cIndex = i.next();
                Clause c = clauses.get(cIndex);

                if (!c.isUnitClause() && c.literals.contains(negated_l)) {
                    c.literals.remove(negated_l);
                    i.remove();
                }
            }

            // if we have removed all instances of the negated literal, clean it up
            if (this.literalMap.get(negated_l).size() == 0)
                this.literalMap.remove(negated_l);
        }
    }


    // find all unit clause instances in the formula; note that a unit clause
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

    // if it exists, find the next instance of a unit clause in the CNF formula; note that a unit
    // clause is simply a clause that contains one literal (!A or A)
    public Clause find_unit_clause() {
        for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
            Clause c = i.next();

            if (c.isUnitClause())
                return c;
        }

        return null;
    }

    // eliminate all pure literals found in the formula & replace them with TRUE unit clauses
    public void eliminate_pure_literals() {
        for (Literal l : this.literalMap.keySet()) {

            if (l.isAssigned) // do not look at literals in TRUE clauses
                continue;

            // if we find an instance of the negated literal, l is NOT pure
            Literal negated_l = l.createNegatedLiteral();
            boolean pure = !this.literalMap.containsKey(negated_l);

            // remove all instances of the pure literal and add it in as a TRUE unit clause
            if (pure) {
                removeLiteral(l); // TODO: if we have removed all literals from a non-unit clause, remove the clause?

                l.setLiteralTrue();
                Clause c = new Clause(l);

                // update data structures
                this.clauses.add(c);
                List<Integer> cIndexList = new LinkedList<Integer>();
                cIndexList.add(clauses.size() - 1);
                this.literalMap.put(l, cIndexList);
            }
        }
    }

    // safely removes a clause in the formula
    public void removeClause(Clause c) {
        int cIndex = this.clauses.indexOf(c);

        if (cIndex != -1) {
            System.out.println("removing clause: " + c);

            this.clauses.remove(c); // remove clause from the set of clauses

            // update map data structure
            Map<Literal, List<Integer>> newLiteralMap = new HashMap<Literal, List<Integer>>();
            int clauseCount = 0;
            for (Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
                Clause aClause = i.next();

                for (Literal lit : aClause.literals) {
                    if (newLiteralMap.containsKey(lit)) {
                        newLiteralMap.get(lit).add(clauseCount);
                    } else {
                        List<Integer> clauseList = new LinkedList<Integer>();
                        clauseList.add(clauseCount);
                        newLiteralMap.put(lit, clauseList);
                    }
                }

                clauseCount++;
            }

            /* old way
            for (Iterator<Literal> i = c.literals.iterator(); i.hasNext();) {
                literalMap.get(i.next()).remove(cIndex);
            }*/
        }
    }

    // removes all instances of a literal from the formula
    public void removeLiteral(Literal l) {
        List<Integer> clauseList = this.literalMap.get(l);
        if (clauseList != null) {
            for (Iterator<Integer> i = clauseList.iterator(); i.hasNext();) {
                int cIndex = i.next();
                Clause c = clauses.get(cIndex);

                if (c.isUnitClause()) {
                    clauses.remove(cIndex); // remove the entire unit clause
                } else {
                    c.literals.remove(l); // remove the literal from the clause
                }
            }

            literalMap.remove(l);
            System.out.println("removing literal: " + l);
        }
    }

    // selects the next literal in the set of literals contained in this CNF formula
    // TODO: apply heuristic here?
    public Literal choose_literal() {
        // from the key set, select a literal
        Set<Literal> setOfLiterals = this.literalMap.keySet();
        Literal[] arrayOfLiterals = setOfLiterals.toArray(new Literal[0]);

        return (arrayOfLiterals.length != 0 ? arrayOfLiterals[0] : null);
    }

    // set all instances of the literal l to "true"
    public void setAllLiteralInstancesTrue(Literal l) {
        l.setLiteralTrue();

        /*if (this.literalMap.containsKey(l)) {
            List<Integer> clauseList = this.literalMap.get(l);

            for (Iterator<Integer> i = clauseList.iterator(); i.hasNext();) {
                int cIndex = i.next();
                Clause c = this.clauses.get(cIndex);

                if (c.isUnitClause())
                    c.setClauseTrue();

                int j = c.literals.indexOf(l);
                c.literals.get(j).setLiteralTrue();
            }


            for(Iterator<Clause> i = this.clauses.iterator(); i.hasNext();) {
                Clause c = i.next();

                if (c.literals.contains(l)) {
                    int j = c.literals.indexOf(l);
                    c.literals.get(j).setLiteralTrue();
                }
            }
        }*/
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
