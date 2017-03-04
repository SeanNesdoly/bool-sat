package boolsat;

/*
 * Given an set of edges in a graph, ThreeColour solves the Three-Colouring Problem,
 * which is described as follows: Given a map, is it possible to colour the countries
 * using three colours such that no two adjacent countries have the same colour?
 * If the problem can be solved, the output is a list of the vertices with their
 * respective colours.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 16th, 2017
*/

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class ThreeColour {


    /* Given a string of input, extracts unique vertices and adds to a set structure. */
    public static Set<String> createVertexSet(String input) {
        Set<String> vertices = new LinkedHashSet<>();
        input = input.substring(1,input.length()-1); // remove braces
        input = input.replace("(", "");
        input = input.replace(")","");
        input = input.replace(",","");
        input = input.replaceAll("\\s+",""); // strip whitespace
        char vertex;
        for (int i = 0; i < input.length(); i++) {
            vertex = input.charAt(i);
            vertices.add(Character.toString(vertex));
        }
        return vertices;
    }

    /* For each vertex, creates 4 clauses imposing colouring constraints. */
    public static ArrayList<String> createVertexClauses(Set<String> vertices) {
        ArrayList<String> clauseList = new ArrayList<>();
        for (String vertex : vertices) {
            clauseList.add("(" + vertex + "R," + vertex + "G," + vertex + "B)");
            clauseList.add("(!" + vertex + "R,!" + vertex + "G)");
            clauseList.add("(!" + vertex + "G,!" + vertex + "B)");
            clauseList.add("(!" + vertex + "B,!" + vertex + "R)");
        }
        return clauseList;
    }

    /* For each vertex, creates 4 clauses imposing colouring constraints. */
    public static ArrayList<String> createEdgeClauses(CNF edges) {
        ArrayList<String> clauseList = new ArrayList<>();

        for (Clause edge : edges.clauses) {
            String vertex1 = edge.literals.get(0).toString();
            String vertex2 = edge.literals.get(1).toString();

            clauseList.add("(!" + vertex1 + "R,!" + vertex2 + "R)");
            clauseList.add("(!" + vertex1 + "G,!" + vertex2 + "G)");
            clauseList.add("(!" + vertex1 + "B,!" + vertex2 + "B)");
        }

        return clauseList;
    }

  /* Takes two lists of clauses and joins them into one string in clause form. */
    public static String joinClauses(ArrayList<String> vertexClauses, ArrayList<String> edgeClauses) {
        String expression = "";
        for (String clause : vertexClauses) {
            expression = expression + clause + ",";
        }
        for (String clause : edgeClauses) {
            expression = expression + clause + ",";
        }
        expression = "{" + expression.substring(0,expression.length()-1) + "}";
        return expression;
    }

    /* Creates a set of vertices and list of edges using CNF object, creates and
    joins the appropriate clauses, then passes the expression to the DPLL solver. */
    public boolean colour(String input) {
        CNF edges = new CNF(input);

        Set<String> vertices = new LinkedHashSet<>();
        for (Literal l : edges.allLiterals) {
            vertices.add(l.toString());
        }

        ArrayList<String> vertexClauseList = createVertexClauses(vertices);
        ArrayList<String> edgeClauseList = createEdgeClauses(edges);
        String clauses = joinClauses(vertexClauseList,edgeClauseList);

        CNF f = new CNF(clauses);
        boolean satisfiable = DPLL.dpll(f);

        return satisfiable;
    }


}
