/*
 * This is the main class that executes the three programs that solve each of the
 * problems that are outlined in the assignment documentation. To run a specific
 * problem, first compile all *.java files in the directory "bool-sat". Then, run
 * the "main" executable on the command line with any of the following as input arguments:
 *
 *      main 1  --> executes Convert to CNF
 *      main 2  --> executes Proof by Refutation
 *      main 3  --> executes Three Colouring problem
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 16th, 2017
 */

public class main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Specify the problem number you would like to execute as a command-line argument (1=ConvertToCNF, 2=ProofByRefutation, 3=ThreeColouringProblem).");
        } else if (args[0].equals("1")) {
            // TODO: add call to ConvertToCNF
        } else if (args[0].equals("2")) {
            // TODO: add call to DPLL
        } else if (args[0].equals("3")) {
            // TODO: add call to ThreeColour
        } else {
            System.out.println("Invalid argument. Enter one of (1,2,3) to execute a problem (1=ConvertToCNF, 2=ProofByRefutation, 3=ThreeColouringProblem).");
        }
    }
}
