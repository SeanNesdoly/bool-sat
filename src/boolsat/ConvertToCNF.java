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

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertToCNF {
    
    private static String[] operators = {"\\^","v","->","<->"};
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
    
   /* Groups expression according to precedence, wrapping parentheses around a group. */
    public static String groupByOperator(String input) { 
        String regex;
        for (String operator : operators) {
            regex = "#";
            System.out.println("input: " + input + " operator: " + operator);
            // brackets on both sides
            if (input.matches(".*" + right + operator + left + ".*")) {
                regex = "(" + left + ".*" + right + operator + left + ".*" + right + ").*";
            }
            // bracket on right side of operator
            else if (input.matches(".*" + operator + left + ".*")) {
                // if no operators before or after operands, ex. Av(B^C)
                if (input.matches("[^" + otherOp + "]+" + operator + left + ".*" + right))
                    regex = "#"; // no need to bracket
                else
                    regex = "[" + otherOp + "]*" + "([^" + otherOp + "]+" + operator + left + ".*" + right + ").*";
            }
            // bracket on left side of operator, ex. (A^B)vC->A
            else if (input.matches(".*" + right + operator + ".*")) {
                // if no operators before or after operands, ex. (A^B)vC
                if (input.matches(left + ".*" + right + operator + "[^" + otherOp + "]+"))
                    regex = "#"; // no need to bracket
                else
                    regex = ".*(" + left + ".*" + right + operator + "[^" + otherOp +"]+).*";
            }
            // general case with no bracketing, ex. AvB^C
            else if (input.matches(".*[^" + otherOp + "]+" + operator + "[^" + otherOp +"]+.*")) {
                System.out.println("matches");
                regex = ".*[" + otherOp + "]*" + "([^" + otherOp + "]+" + operator + "[^" + otherOp +"]+).*";
            }
            if (input.matches(regex)) {
                System.out.println("regex: " + regex);
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(input);
                int i = 0;
                while (matcher.find()) {
                    i++;
                    System.out.println("group " + i + ": " + matcher.group(i));
                    input = input.replace(matcher.group(i),"(" + matcher.group(i) + ")");

                }
              }
            }       
        return input;
    }
    
    public static void main(String[] args) {
        addEquivalences();
        String input = "(A<->B)";
        input = input.replaceAll("\\s+",""); // get rid of any whitespace
        //String groupedInput = groupByOperator(input);
        //System.out.println(groupedInput);
        //String convertedInput = convertToCNF(input);
        //System.out.println("Converted to CNF: " + convertedInput);
        String convertedInput = "(AvB)^(BvC)";
        String clauseForm = writeInClauseForm(convertedInput);
        System.out.println("Clause form: " + clauseForm);
    }
    
}
