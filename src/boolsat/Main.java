package boolsat;

/*
 * This is the main class that executes the three programs that solve each of the
 * problems that are outlined in the assignment documentation. To run a specific
 * problem, first compile all *.java files in the package "boolsat". Then, run
 * the "Main" executable on the command line with any of the following as input arguments:
 *
 *      java Main 1  --> executes Convert to CNF
 *      java Main 2  --> executes Proof by Refutation
 *      java Main 3  --> executes Three Colouring problem
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 16th, 2017
 */

import java.util.LinkedList;
import java.util.ArrayList;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Provide the desired problem number you would like to run as a command-line argument:\n\t1=ConvertToCNF\n\t2=ProofByRefutation\n\t3=ThreeColouringProblem");
        } else if (args[0].equals("1")) {
            // TODO: add call to ConvertToCNF
            System.out.println("Running Convert to CNF (#1)\n============================");
        } else if (args[0].equals("2")) {
            // TODO: add call to DPLL
            System.out.println("Running Proof by Refutation (#2)\n=================================");


            ArrayList<String> formulas = null;
            try {
                formulas = TextFile.readFile();
                String formula;
                /*for (int i = 0; i < formulas.size() - 1; i++) {
                    // TODO: parse formulas into clausal CNF from Mary
                }
                // handle final statement of the form "Therefore, A."
                // TODO: negate this statement for proof by refutation!
                String conclusion = formulas.get(formulas.size()-1);
                conclusion = conclusion.substring(12, conclusion.length());*/

                CNF cnf = new CNF(formulas.get(0));
                System.out.println(cnf.formula); // raw string
                System.out.println(cnf);

                boolean satisfiable = DPLL.dpll(cnf);

                // TODO: for Proof By Refutation, swap the below two statements; returning false
                // dpll denotes that the conlcusion follows logically from the set of premises!
                if (satisfiable) {
                    System.out.println("\nSATISFIABLE");
                    System.out.println("\nAn assignment of literals that makes the formula True:");
                    for (Literal l : DPLL.assignments) {
                        System.out.println("\t" + l + "=" + l.computeValue() + "\t\tval=" + l.val + "\tbAssigned=" + l.isAssigned);
                    }
                } else
                    System.out.println("\nUNSATISFIABLE");

                // write the computed satisfiability of the problem instance to file
                if (satisfiable)
                    TextFile.writeFile(DPLL.UNSAT);
                else
                    TextFile.writeFile(DPLL.SAT);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                return;
            }


        } else if (args[0].equals("3")) {
            // TODO: add call to ThreeColour
            System.out.println("Running Three-colouring Problem (#3)\n=====================================");
        } else {
            System.out.println("Invalid argument. Enter the desired problem number you would like to run:\n\t1=ConvertToCNF\n\t2=ProofByRefutation\n\t3=ThreeColouringProblem");
        }
    }
}
