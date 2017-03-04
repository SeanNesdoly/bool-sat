package boolsat;

/*
 * This is the main class that executes the three programs that solve each of the
 * problems that are outlined in the assignment documentation. To run a specific
 * problem, run any of the following command line arguments:
 *
 *      ant -Darg=1  --> executes Convert to CNF
 *      ant -Darg=2  --> executes Proof by Refutation
 *      ant -Darg=3  --> executes Three Colouring problem
 *
 * Or, run the following, where n is the problem you would like to run:
 *
 * ant compile
 * java -jar build/bool-sat.jar n
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
            System.out.println("Running Convert to CNF (#1)\n============================");

            try {
                ArrayList<String> formula = TextFile.readFile();
                String cnf = ConvertToCNF.processInput(formula.get(0));
                TextFile.writeFile(cnf);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                return;
            }

        } else if (args[0].equals("2")) {
            System.out.println("Running Proof by Refutation (#2)\n=================================");

            ArrayList<String> formulas = null;
            try {
                // parse all lines into a clausal CNF formula
                formulas = TextFile.readFile();
                String formula = "";
                for (int i = 0; i < formulas.size() - 1; i++) {
                    String cnfF = ConvertToCNF.processInput(formulas.get(i));

                    int length = cnfF.length();
                    if (i == 0) { // parse first clause
                        formula = cnfF.substring(0,length-1);
                    } else { // parse all other clauses
                        formula += "," + cnfF.substring(1,length-1);
                    }
                }
                // negate final statement of the form "Therefore, A."
                String conclusion = formulas.get(formulas.size()-1);
                conclusion = "!(" + conclusion.substring(11, conclusion.length()-1) + ")";
                conclusion = ConvertToCNF.processInput(conclusion);
                formula += "," + conclusion.substring(1,conclusion.length()-1) + "}";
                System.out.println("\ndpll input formula: " + formula);

                CNF cnf = new CNF(formula);

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

                // Write the computed satisfiability of the problem instance to file:
                //     if dpll returns true, the conclusion does NOT follow from the premises.
                //     if dpll returns false, the conclusion does follow from the premises
                if (satisfiable)
                    TextFile.writeFile(DPLL.UNSAT);
                else
                    TextFile.writeFile(DPLL.SAT);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                return;
            }

        } else if (args[0].equals("3")) {
            System.out.println("Running Three-colouring Problem (#3)\n=====================================");

            try {
                ArrayList<String> formula = TextFile.readFile();

                ThreeColour c = new ThreeColour();
                boolean canColour = c.colour(formula.get(0));

                if (canColour) {
                    String colours = "{";
                    for (Literal l : DPLL.assignments) {
                        if (l.val) {
                            colours += l.s + ",";
                        }
                    }

                    colours = colours.substring(0,colours.length() - 1) + "}";
                    TextFile.writeFile(colours);
                } else {
                    TextFile.writeFile("The graph cannot be three-colored.");
                }
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                return;
            }

        } else {
            System.out.println("Invalid argument. Enter the desired problem number you would like to run:\n\t1=ConvertToCNF\n\t2=ProofByRefutation\n\t3=ThreeColouringProblem");
        }
    }
}
