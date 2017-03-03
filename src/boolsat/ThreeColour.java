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
//package threecolour;

import java.io.IOException;
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
    
    /* Given a string of input, pairs vertices to create list of edges. */
    public static ArrayList<String> createEdgeList(String input) {
        ArrayList<String> edgeList = new ArrayList<>();
        input = input.substring(1,input.length()-1); // remove braces
        input = input.replace(",","");
        // not finished
        
    }
    
    /* For each vertex, creates 4 clauses imposing colouring constraints. */
    public static ArrayList<String> createVertexClauses(Set<String> vertices) {
        ArrayList<String> clauseList = new ArrayList<>();
        for (String vertex : vertices) {
            clauseList.add(vertex + "Rv" + vertex + "Gv" + vertex + "B");
            clauseList.add("!" + vertex + "Rv!" + vertex + "G");
            clauseList.add("!" + vertex + "Gv!" + vertex + "B");
            clauseList.add("!" + vertex + "Bv!" + vertex + "R");
        }
        return clauseList;
    }
    
        /* For each vertex, creates 4 clauses imposing colouring constraints. */
    public static ArrayList<String> createEdgeClauses(Set<String> edgeList) {
        ArrayList<String> clauseList = new ArrayList<>();
        for (String edge : edgeList) {

        }
        return clauseList;
    }
    
    public static void main(String[] args) {
        String input = "{(A,B),(A,C),(A,D),(B,C),(B,D),(C,D)}";
        Set<String> vertices = createVertexSet(input);
        ArrayList<String> edges = createEdgeList(input);
        ArrayList<String> clauseList = createVertexClauses(vertices);

    }
    
    
}
