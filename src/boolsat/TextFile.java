package boolsat;

/*
 * A utility class that handles input & output from a text file. All methods
 * within this class are static, and thus it should not be instantiated as an
 * object.
 *
 * TODO: ?Specifically, this class is tailored to reading formulae from propositional
 * logic; these are then be converted into CNF. A further adaptation exists to
 * read in a set of edges from a graph for use in the Three-colouring problem.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 11th, 2017
 */

 import java.util.Scanner;
 import java.io.IOException;
 import java.io.FileWriter;
 import java.io.PrintWriter;
 import java.io.File;
 import java.io.FilenameFilter;
 import java.util.ArrayList;

public class TextFile {

    private static final String OUT = "out.txt";

    private TextFile() {}

    /**
    * Reads the contents of the first file in the source directory that has a
    * *.txt extension. Each line of text is added as an element to an ArrayList.
    *
    * @return an ArrayList of strings, where each element is a line of text
    */
    public static ArrayList<String> readFile() throws IOException {
        // select the first *.txt file in the current directory
        File directory = new File("./");
        String[] files = directory.list(new FilenameFilter() {
            public boolean accept(File directory, String fileName) {
                return fileName.endsWith(".txt");
            }
        });

        Scanner sf;
        if (files.length != 0)
            sf = new Scanner(new File(files[0]));
        else // no *.txt file found
            throw new IOException("There does not exist an input file with the extension *.txt in the source directory.");

        ArrayList<String> lines = new ArrayList<String>();
        while (sf.hasNext()) {
            lines.add(sf.nextLine());
        }

        // clean up open streams
        sf.close();

        return lines;
    }

    /**
     * Writes to a file whose relative path is denoted by the class constant OUT.
     *
     * @param outTxt the text to write to file
     * @return void
     */
     public static void writeFile(String outTxt) throws IOException {
         FileWriter fw = new FileWriter(OUT);
         PrintWriter output = new PrintWriter(fw);

         output.print(outTxt);

         // clean up open streams
         output.close();
         fw.close();

         return;
     }
}
