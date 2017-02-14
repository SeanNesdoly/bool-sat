/*
 * An implementation of the DPLL Algorithm for deciding the satisfiability of
 * propositional logic formulae in conjuctive normal form.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 11th, 2017
 */

import java.util.ArrayList;
import java.io.IOException;

public class DPLL {

    private static final String SATISFIABLE = "The conclusion follows logically from the premises.\n";
    private static final String NOT_SATISFIABLE = "The conclusion does not follow logically from the premises.\n";

    public static void main(String[] args) {

        ArrayList<String> cnf_string_formula = null;
        try {
            cnf_string_formula = TextFile.readFile();
            TextFile.writeFile(NOT_SATISFIABLE);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return;
        }

        //parseCNF(cnf_string_formula);
    }

    public static char[][] parseCNF(String cnf_string_formula) {
        // convert to all uppercase & trim off leading & ending braces
        cnf_string_formula = cnf_string_formula.toUpperCase().substring(1,cnf_string_formula.length()-1);

        //String delim = "(\\)\\W\\()";
        String delim = ",";
        String[] clauses = cnf_string_formula.split(delim);

        char[][] cnf_formula = new char[clauses.length][];

        for (int i = 0; i < clauses.length; i++) {
            System.out.println(clauses[i]);
            String[] literals = clauses[i].split(",");
            cnf_formula[i] = new char[literals.length];

            for (int j = 0; j < literals.length; j++) {
                if (literals[j].length() > 1) {
                    // case: false literal !A --> convert to LOWERCASE
                    //cnf_formula[i][j] = literals[j].toLowerCase().charAt(1);
                } else {
                    // case: true literal A --> keep UPPERCASE
                    //cnf_formula[i][j] = literals[j].charAt(0);
                }
            }
        }

        return cnf_formula;
    }

    private class Symbol {
        public char _s;
        public boolean _value;

        Symbol(char s, boolean value) {
            _s = s;
            _value = value;
        }
    }
 }
