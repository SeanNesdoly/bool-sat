import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * An implementation of the DPLL Algorithm for deciding the satisfiability of
 * propositional logic formulae in conjuctive normal form.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 11th, 2017
 */

public class DPLL {

    private static final String IN = "in2.txt";
    private static final String OUT = "out2.txt";

    public static void main(String[] args) {

        try {
            System.out.println(readFile());
            writeFile(false);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

   /**
    * Reads from the file with the name defined by the constant IN. The file must
    * contain a set of premises and a conclusion, each seperated by a new-line character.
    * The set of premises and the conclusion are returned as an ArrayList of strings.
    *
    * @return an array of premises and a conclusion
    */
    public static String readFile() throws IOException {
        Scanner sf = new Scanner(new File(IN));

        // for testing purposes only; will replace
        String premises = sf.nextLine();

        /* TODO: code below runs on non-CNF input; will eventually replace the code above!
        ArrayList<String> premises = new ArrayList<String>();
        while (sf.hasNext()) {
            premises.add(sf.nextLine());
        }*/

        // clean up
        sf.close();

        return premises;
    }

   /**
    * Writes to a file named by the constant OUT. The file will contain a single
    * line that denotes whether or not the conlusion logically followed from the
    * premises (where the conclusion and premises were obtained from the IN file).
    */
    public static void writeFile(boolean conclusion) throws IOException {
        FileWriter fw = new FileWriter(OUT);
        PrintWriter output = new PrintWriter(fw);

        if (conclusion)
            output.print("The conclusion follows logically from the premises.\n");
        else
            output.print("The conclusion does not follow logically from the premises.\n");

        // clean up
        output.close();
        fw.close();
        return;
    }

 }
