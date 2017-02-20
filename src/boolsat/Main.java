package boolsat;

/*
 * This is the main class that executes the three programs that solve each of the
 * problems that are outlined in the assignment documentation. To run a specific
 * problem, first compile all *.java files in the directory "bool-sat". Then, run
 * the "main" executable on the command line with any of the following as input arguments:
 *
 *      java main 1  --> executes Convert to CNF
 *      java main 2  --> executes Proof by Refutation
 *      java main 3  --> executes Three Colouring problem
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 16th, 2017
 */

import java.util.*;
import java.io.IOException;
import boolsat.*;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Provide the desired problem number you would like to run as a command-line argument:\n\t1=ConvertToCNF\n\t2=ProofByRefutation\n\t3=ThreeColouringProblem");
        } else if (args[0].equals("1")) {
            // TODO: add call to ConvertToCNF
            System.out.println("running problem 1");
        } else if (args[0].equals("2")) {
            // TODO: add call to DPLL
            System.out.println("running problem 2");


            ArrayList<String> formulas = null;
            try {
                formulas = TextFile.readFile();
                //System.out.println(formulas.get(0));

                // TODO: parse formulas into clausal CNF from Mary

                DPLL.CNF cnf = new DPLL().new CNF(formulas.get(0));
                System.out.println(cnf.formula);
                System.out.println(cnf);

                /*for (Literal lit:cnf.allLiterals)
                    System.out.println("~" + lit);*/


                ArrayList<DPLL.Clause> all_unit_clauses = cnf.find_all_unit_clauses();
                for (DPLL.Clause unit_clause : all_unit_clauses) {
                    cnf = DPLL.unit_propagate(unit_clause.toLiteral(), cnf);
                    System.out.println(cnf);
                }

                TextFile.writeFile(DPLL.SAT);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                return;
            }


        } else if (args[0].equals("3")) {
            // TODO: add call to ThreeColour
            System.out.println("running problem 3");
        } else {
            System.out.println("Invalid argument. Enter the desired problem number you would like to run:\n\t1=ConvertToCNF\n\t2=ProofByRefutation\n\t3=ThreeColouringProblem");
        }
    }
}
