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

public class main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Provide the desired problem number you would like to run as a command-line argument:\n\t1=ConvertToCNF\n\t2=ProofByRefutation\n\t3=ThreeColouringProblem");
        } else if (args[0].equals("1")) {
            // TODO: add call to ConvertToCNF
            System.out.println("running problem 1");
        } else if (args[0].equals("2")) {
            // TODO: add call to DPLL
            System.out.println("running problem 2");
        } else if (args[0].equals("3")) {
            // TODO: add call to ThreeColour
            System.out.println("running problem 3");
        } else {
            System.out.println("Invalid argument. Enter the desired problem number you would like to run:\n\t1=ConvertToCNF\n\t2=ProofByRefutation\n\t3=ThreeColouringProblem");
        }
    }
}
