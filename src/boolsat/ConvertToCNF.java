/*
 * ConvertToCNF takes an input formula, converts it to Conjunctive Normal Form,
 * and returns it in clause form.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 11th, 2017
 * 
 * TODO: properly group multiple occurences of the same operator (A^BvA^C)
 * TODO: ensure that simple expressions are still grouped, ex. A^B is (A^B) 
*/
package converttocnf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertToCNF {
    
    private static ArrayList<String> operators = new ArrayList<>();
    private static LinkedHashMap<String,String> equivalenceMap = new LinkedHashMap();
    private static String left = Pattern.quote("(");
    private static String right = Pattern.quote(")"); 
    private static String otherOp = Pattern.quote("^v<->");
    
    public static void addEquivalences() {
        equivalenceMap.put(left + "(.+)<->(.+)" + right,"({1}->{2})^({2}->{1})");
        equivalenceMap.put(left + "(.+)->(.+)" + right,"!{1}v{2}");
        equivalenceMap.put("!" + left + "(.+)\\^(.+)" + right,"!{1}v!{2}");
        equivalenceMap.put("!" + left +"(.+)v(.+)" + right, "!{1}^!{2}");
        equivalenceMap.put("!!(.+)","{1}");
        equivalenceMap.put("(.+)v" + left + "(.+)\\^(.+)" + right,"({1}v{2})^({1}v{3})");
    }
    
    public static void populateOperatorList() {
        operators.add("\\^");
        operators.add("v");
        operators.add("->");
        operators.add("<->");
    }
 
    /* Takes input in CNF and converts it into clause form. */
    public static String writeInClauseForm(String convertedInput) {
        // extract all groups of the form (AvB)
        String clauseForm;
        clauseForm = convertedInput.replaceAll("v", ",");
        clauseForm = clauseForm.replaceAll("\\^", ",");
        clauseForm = "{" + clauseForm + "}";
        return clauseForm;
    }
    
    
    /* Takes grouped expression and converts it to CNF based on equivalence rules. */
    public static String convertToCNF(String groupedInput) {
        for (String leftSide : equivalenceMap.keySet()) {
            if (groupedInput.matches(leftSide)) {
                System.out.println(leftSide);
                String rightSide = equivalenceMap.get(leftSide);
                Pattern pattern = Pattern.compile(leftSide);
                Matcher matcher = pattern.matcher(groupedInput);
                if (matcher.find()) {
                    groupedInput = rightSide.replace("{1}", matcher.group(1));
                    if (rightSide.contains("{2}"))
                        groupedInput = groupedInput.replace("{2}", matcher.group(2));
                    if (rightSide.contains("{3}"))
                        groupedInput = groupedInput.replace("{3}", matcher.group(3));
                    System.out.println(groupedInput);
                }
            }
        }
        return groupedInput;
    }    
    
    /* Returns list of all operators used in a particular expression. */
    public static ArrayList<String> findOperators(String input) {
        ArrayList<String> inputOperators = new ArrayList<>();
        for (String operator : operators) {
            if (input.matches(".*\\w+" + operator + "\\w+.*"))
                inputOperators.add(operator);         
        }
        return inputOperators;
    }
    
    
   /* Groups expression according to precedence, wrapping parentheses around a group. */
    public static String groupByOperator(String input, ArrayList<String> inputOperators) {     
        ArrayList<String> otherOperators;
        for (String operator : inputOperators) {
            otherOperators = (ArrayList<String>)inputOperators.clone();
            if (operator.equals("\\^") || operator.equals("v")) {
                // don't recognize self as another operator when parsing
                otherOperators.remove(operator);
            }
            Pattern pattern = Pattern.compile("(" + operator + ")");
            Matcher matcher = pattern.matcher(input);
            int i = 0;
            int j,k;
            while (matcher.find()) {
                System.out.println("group: " + matcher.group(1));
                j = matcher.start();
                k = matcher.end();
                i++;
             }       
         }
        
        return input;
    }
    
    /* Takes a formula and groups it by operator, converts it to CNF, and writes 
    it in clause form. */
    public static void processInput(String formula) {
        ArrayList<String> inputOperators = findOperators(formula); 
        // if the only operator is '^' or 'v', no grouping is required
        if (inputOperators.size() == 1) {
            if (inputOperators.contains("\\^") || inputOperators.contains("v")) {
                // if formula is string of v's, bracket whole expression
                if (inputOperators.contains("v"))
                    formula = "(" + formula + ")";
            }
        }
        else { 
            formula = groupByOperator(formula, inputOperators);
            
            //String convertedInput = convertToCNF(input);
            //System.out.println("Converted to CNF: " + convertedInput);
            //String convertedInput = "fish->tasty^healthy";
        }
        System.out.println(formula);
        //String clauseForm = writeInClauseForm(convertedInput);
        //System.out.println("Clause form: " + clauseForm);
    
    }
    
    /* Populates array lists and preps input for parsing. */
    public static void main(String[] args) {
        populateOperatorList();
        ArrayList<String> input = null;
        String formula = "AvB<->AvC";
        /*
        try {
            input = TextFile.readFile();
            formula = input.get(0);
            System.out.println(formula);
            
            
            
            
            TextFile.writeFile("bud");
        }
        catch (IOException ex) {
            System.err.println(ex.getMessage());
            return; 
        }
*/
        //addEquivalences();
        formula = formula.replaceAll("\\s+",""); // get rid of any whitespace
        processInput(formula);
     
        }
    }
    
