/*
 * ConvertToCNF takes an input formula, converts it to Conjunctive Normal Form,
 * and returns it in clause form.
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 11th, 2017
 * 
*/
package converttocnf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertToCNF {
    
    private static ArrayList<String> operators = new ArrayList<>();
    private static String opCharacter = Pattern.quote("^v<->");
    private static String nonLiteral = Pattern.quote("^v<-()");
    private static String left = Pattern.quote("(");
    private static String right = Pattern.quote(")");
    
    
    public static void populateOperatorList() {
        operators.add("\\^");
        operators.add("v");
        operators.add("->");
        operators.add("<->");
    }
 
    /* Takes input in CNF and converts it into clause form. */
    public static String writeInClauseForm(String convertedInput) {
        String clauseForm;
        clauseForm = convertedInput.replaceAll("v", ",");
        clauseForm = clauseForm.replaceAll("\\^", ",");
        clauseForm = "{" + clauseForm + "}";
        return clauseForm;
    }
    
    
    /* Takes grouped expression and converts it to CNF based on equivalence rules. */
    public static String convertToCNF(String groupedInput) {
        while (groupedInput.matches(".+<->.+")) {
            groupedInput = convertImplicationOrIFF(groupedInput,"<->");
            System.out.println(groupedInput);
        }
        while (groupedInput.matches(".+->.+")) {
                groupedInput = convertImplicationOrIFF(groupedInput,"->");
                System.out.println(groupedInput);
        }
        while (groupedInput.matches(".*!" + left + ".+\\^.+" + right + ".*")) {
            groupedInput = distributeNot(groupedInput,"\\^");
            System.out.println(groupedInput);
        }
        while (groupedInput.matches(".*!" + left + ".+v.+" + right + ".*")) {
            groupedInput = distributeNot(groupedInput,"v");
            System.out.println(groupedInput);
        }
        while (groupedInput.matches(".*!!.+")) {
            groupedInput = removeDoubleNegation(groupedInput);
        }
        while (groupedInput.matches(".+v" + left + ".+\\^.+" + right + ".*") ||
                groupedInput.matches(".*" + left + ".+\\^.+" + right + "v.+")) {
            groupedInput = distributeOr(groupedInput);
        }  
        return groupedInput;
    }   
    
    /* Converts expressions of the form A<->B to (A->B)^(B->A), and expressions of 
    the form A->B to !AvB 
    public static String convertImplicationOrIFF(String groupedInput, String operator) {
        String group;
        while (groupedInput.matches(".+" + operator + ".+")) {
            Pattern pattern = Pattern.compile("([^"+ opCharacter + "]+" + operator + "[^"+ opCharacter + "]+)");
            Matcher matcher = pattern.matcher(groupedInput);
            while (matcher.find()) {
                group = matcher.group(1);
                System.out.println("group: " + group);
                Pattern subPattern = Pattern.compile("(.+)" + operator + "(.+)");
                Matcher subMatcher = subPattern.matcher(group);
                if (subMatcher.find()) {
                    String newGroup;
                    String equivalence;
                    if (operator.equals("<->")) 
                        equivalence  = "({1}->{2})^({2}->{1})";
                    else
                        equivalence = "!{1}v{2}";
                    newGroup = equivalence.replace("{1}", subMatcher.group(1));
                    newGroup = newGroup.replace("{2}", subMatcher.group(2));
                    groupedInput = groupedInput.replace(group,newGroup);  
                    System.out.println(groupedInput);
                }                
            }
        }
        return groupedInput;   
    }
    */
    
        /* Converts expressions of the form A<->B to (A->B)^(B->A), and expressions of 
    the form A->B to !AvB */
    public static String convertImplicationOrIFF(String groupedInput, String operator) {
        ArrayList<String> otherOperators = findOperators(groupedInput);
        if (otherOperators.contains("!"))
            otherOperators.remove("!");
        String group1;
        String group2;
        String equivalence;
        if (operator.equals("<->")) 
            equivalence  = "({1}->{2})^({2}->{1})";
        else
            equivalence = "!{1}v{2}";
        while (groupedInput.matches(".+" + operator + ".+")) {
            Pattern pattern = Pattern.compile(operator);
            Matcher matcher = pattern.matcher(groupedInput);
            if (matcher.find()) {
                int opStartIndex = matcher.start();
                int opEndIndex = matcher.end();
                int groupStartIndex = modifiedScanBackwards(opStartIndex,groupedInput,otherOperators);
                if (groupStartIndex == 1 && groupedInput.charAt(0) == '!')
                    groupStartIndex = 0;
                System.out.println("start: " + groupStartIndex);
                group1 = groupedInput.substring(groupStartIndex,opStartIndex);
                int groupEndIndex = scanForwards(opEndIndex,groupedInput,otherOperators);
                if (groupEndIndex == groupedInput.length()-1)
                    groupEndIndex++;
                group2 = groupedInput.substring(opEndIndex,groupEndIndex);
                String subExpression = groupedInput.substring(groupStartIndex,groupEndIndex);
                System.out.println(subExpression);
                System.out.println(group1);
                System.out.println(group2);
                
                String newSubExpression = equivalence.replace("{1}", group1);
                newSubExpression = newSubExpression.replace("{2}", group2);
                groupedInput = groupedInput.replace(subExpression,newSubExpression);
                /*
                group = matcher.group(1);
                System.out.println("group: " + group);
                Pattern subPattern = Pattern.compile("(.+)" + operator + "(.+)");
                Matcher subMatcher = subPattern.matcher(group);
                if (subMatcher.find()) {
                    String newGroup;
                    String equivalence;
                    if (operator.equals("<->")) 
                        equivalence  = "({1}->{2})^({2}->{1})";
                    else
                        equivalence = "!{1}v{2}";
                    newGroup = equivalence.replace("{1}", subMatcher.group(1));
                    newGroup = newGroup.replace("{2}", subMatcher.group(2));
                    groupedInput = groupedInput.replace(group,newGroup);  
                    System.out.println(groupedInput);
                }                
            }
                */
        }
        }
        return groupedInput;   
    }
    
    /* Converts expressions of the form !(A^B) to !Av!B, or !(AvB) to !A^!B */
    public static String distributeNot(String groupedInput, String operator) {
        String group;
        while (groupedInput.matches(".*!" + left + ".+" + operator + ".+" + right + ".*")) {
            Pattern pattern = Pattern.compile("(!" + left + "[^" + left + right + "]+" + operator + "[^" + left + right + "]+" + right + ")");
            Matcher matcher = pattern.matcher(groupedInput);
            while (matcher.find()) {
                group = matcher.group(1);
                System.out.println("group: " + group);
                Pattern subPattern = Pattern.compile(left + "(.+)" + operator + "(.+)" + right);
                Matcher subMatcher = subPattern.matcher(group);
                if (subMatcher.find()) {
                    String newGroup;
                    String equivalence;
                    if (operator.equals("\\^")) 
                        equivalence  = "!{1}v!{2}";
                    else
                        equivalence = "!{1}^!{2}";
                    
                    newGroup = equivalence.replace("{1}", subMatcher.group(1));
                    newGroup = newGroup.replace("{2}", subMatcher.group(2));
                    groupedInput = groupedInput.replace(group,newGroup);
                    
                }                
            }
        }
        groupedInput = regroupFormula(groupedInput);
        return groupedInput;
    }
    
    public static String regroupFormula(String groupedInput) {
        ArrayList<String>inputOperators = findOperators(groupedInput);
        if (inputOperators.contains("!"))
            inputOperators.remove("!");
        if (!(inputOperators.size() == 1 && inputOperators.contains("v"))) 
            groupedInput = groupByOperator(groupedInput,inputOperators);
        return groupedInput;
    }
    
    /* Converts terms of the form !!A into A */
    public static String removeDoubleNegation(String groupedInput) {
        String group;
        while (groupedInput.matches(".*!!.+")) {
            Pattern pattern = Pattern.compile("(!![^" + nonLiteral + "]+)");
            Matcher matcher = pattern.matcher(groupedInput);
            while (matcher.find()) {
                group = matcher.group(1);
                System.out.println("group: " + group);
                Pattern subPattern = Pattern.compile("!!([^" + nonLiteral + "]+)");
                Matcher subMatcher = subPattern.matcher(group);
                if (subMatcher.find()) {                   
                    String equivalence = "{1}";
                    String newGroup = equivalence.replace("{1}", subMatcher.group(1));
                    groupedInput = groupedInput.replace(group,newGroup);   
                } 
            }
        }
        return groupedInput;     
    }
    
    /* Converts expressions of the form Av(B^C) or (B^C)vA to: (AvB)^(AvC) */
    public static String distributeOr(String groupedInput) {
        String generalRegex;
        String groupRegex;
        String subGroupRegex;
        String group;
        String equivalence;
        if (groupedInput.matches(".+v" + left + ".+\\^.+" + right + ".*")) {
            generalRegex = ".+v" + left + ".+\\^.+" + right + ".*";
            groupRegex = "(" + "[^" + nonLiteral + "]+v" + left + "[^" + nonLiteral + "]+\\^[^" + nonLiteral + "]+" + right + ")";
            subGroupRegex = "([^" + nonLiteral + "]+)v" + left + "([^" + nonLiteral + "]+)\\^([^" + nonLiteral + "]+)" + right;
            equivalence = "({1}v{2})^({1}v{3})";
        }
        else {
            generalRegex = ".*" + left + ".+\\^.+" + right + "v.+";
            groupRegex = "(" + left + "[^" + nonLiteral + "]+\\^[^" + nonLiteral + "]+" + right + "v[^" + nonLiteral + "]+)";
            subGroupRegex = left + "([^" + nonLiteral + "]+)\\^([^" + nonLiteral + "]+)" + right + "v([^" + nonLiteral + "]+)";
            equivalence = "({3}v{1})^({3}v{2})";
        }
        while (groupedInput.matches(generalRegex)) {
            Pattern pattern = Pattern.compile(groupRegex);
            Matcher matcher = pattern.matcher(groupedInput);
            while (matcher.find()) {
                group = matcher.group(1);
                System.out.println("group: " + group);
                Pattern subPattern = Pattern.compile(subGroupRegex);
                Matcher subMatcher = subPattern.matcher(group);
                if (subMatcher.find()) {
                    String newGroup = equivalence.replace("{1}", subMatcher.group(1));
                    newGroup = newGroup.replace("{2}", subMatcher.group(2));
                    newGroup = newGroup.replace("{3}", subMatcher.group(3));
                    groupedInput = groupedInput.replace(group,newGroup);                
                }                
            }
        }
        return groupedInput;
    }  
    
    /* Returns list of all operators used in a particular expression. */
    public static ArrayList<String> findOperators(String input) {
        ArrayList<String> inputOperators = new ArrayList<>();
        // check for ! first
        if (input.matches(".*!.*"))
            inputOperators.add("!");
        // now check all binary operators
        for (String operator : operators) {
            if (input.matches(".*[^" + opCharacter + "]+" + operator + "[^" + opCharacter + "]+.*"))
                inputOperators.add(operator);         
        }
        return inputOperators;
    }
    
        /* Given an input string and starting index j, scans backward in the string.
    Returns an appropriate position for a left parenthesis to be placed. */
    public static int scanBackwards(int j,String input,ArrayList<String> otherOperators) {
        int rightCount = 0;
        int leftCount = 0;
        boolean foundOperator = false;
        String literalChar;
        char character = input.charAt(--j);
        while (j > 0) {
            if (character == ')')
                rightCount++;
            if (character == '(') {
                leftCount++;
                if (leftCount == rightCount)
                    break;
            }
            // if character is part of another operator 
            for (String operator : otherOperators) {
                literalChar = Pattern.quote(Character.toString(character));
                if (operator.matches(".*" + literalChar + ".*"))  {
                    foundOperator = true;
                    break;
                }
            }
            if (foundOperator && rightCount == 0)
                break;
            character = input.charAt(--j); // decrement then get char
        }
        return j;     
    }
    
    /* Given an input string and starting index j, scans backward in the string.
    Modified to return the starting position of a bracketed group. */
    public static int modifiedScanBackwards(int j,String input,ArrayList<String> otherOperators) {
        boolean foundOperator = false;
        boolean rightParenthesis = false;
        String literalChar;
        char character = input.charAt(--j);
        while (j > 0) {
            if (character == ')') 
                rightParenthesis = true;
            if (character == '(') {
                if (rightParenthesis)
                    break;
            }
            // if character is part of another operator 
            for (String operator : otherOperators) {
                literalChar = Pattern.quote(Character.toString(character));
                if (operator.matches(".*" + literalChar + ".*"))  {
                    foundOperator = true;
                    break;
                }
            }
            if (foundOperator && !rightParenthesis)
                break;
            character = input.charAt(--j); // decrement then get char
        }
        return j;     
    }
    
    
    /* Given an input string and starting index k, scans forward in the string.
    Returns an appropriate position for a right parenthesis to be placed. */
    public static int scanForwards(int k, String input, ArrayList<String> otherOps) {
        int leftCount = 0;
        int rightCount = 0;
        boolean foundOp = false;
        String literalChar;
        char character;
        while (k < input.length()) {
            character = input.charAt(k); 
            if (character == '(')
                leftCount++;
            if (character == ')') {
                rightCount++;
                if (rightCount == leftCount)
                    break;
            }
            for (String op : otherOps) {
                literalChar = Pattern.quote(Character.toString(character));
                if (op.matches(".*" + literalChar + ".*")) {
                    foundOp = true;
                    break;
                }
            }
            if (foundOp && leftCount == 0)
                break;
            k++;
               
        }
        return k;
    }
    
   /* Groups expression according to precedence, wrapping parentheses around a group. */
    public static String groupByOperator(String input, ArrayList<String> inputOperators) {     
        ArrayList<String> otherOperators;
        inputOperators.remove("!");
        for (String operator : inputOperators) {
            otherOperators = (ArrayList<String>)inputOperators.clone();
            if (operator.equals("\\^") || operator.equals("v")) {
                // don't recognize self as another operator when scanning
                otherOperators.remove(operator);
            }
            Pattern pattern = Pattern.compile("(" + operator + ")");
            Matcher matcher = pattern.matcher(input);
            int bracketCount = 0;
            int j,k;
            while (matcher.find()) {
                j = matcher.start() + bracketCount;
                k = matcher.end() + bracketCount;              
                j = scanBackwards(j,input,otherOperators);
                k = scanForwards(k,input,otherOperators);
                // if brackets would group the entire expression, or there are already brackets there
                if ((input.charAt(j) == '(' || input.charAt(j) == '!' || input.charAt(j+1) == '(') && input.charAt(k-1) == ')')
                    continue;
                else if (j == 0 && k >= input.length()-1)
                    continue;
                else if (j == 0) {
                    input = "(" + input;
                    input = input.substring(0,k+1) + ")" + input.substring(k+1);
                }
                else if (input.charAt(j-1) == '!') 
                    continue;
                else if (k == input.length()) {
                    input = input.substring(0,j+1) + "(" + input.substring(j+1);
                    input = input + ")";                   
                }
                else {
                    input = input.substring(0,j+1) + "(" + input.substring(j+1);
                    input = input.substring(0,k+1) + ")" + input.substring(k+1);         
                }
                bracketCount = bracketCount + 2;
                System.out.println(input);
             }  
         } 
        return input;
    }
    
    /* Given an input formula, returns true if expression is already grouped */
    public static boolean findParentheses(String formula) {
        boolean foundParenthesis = false;
        for (int i = 0; i < formula.length(); i++) {
            if (formula.charAt(i) == '(' || formula.charAt(i) == ')')
                foundParenthesis = true;
        }
        return foundParenthesis;
    }
    
    /* Takes a formula and groups it by operator, converts it to CNF, and writes it in clause form. */
    public static void processInput(String formula) {
        ArrayList<String> inputOperators = findOperators(formula); 
        boolean parenthesesPresent = findParentheses(formula);
        // if there are parentheses present, skip grouping
        if (parenthesesPresent) {
            // if the only operator is '^' or 'v', skip grouping
            if (!(inputOperators.size() == 1 && ((inputOperators.contains("\\^") || inputOperators.contains("v")))))
                formula = convertToCNF(formula);
        }
        else {
            if (!(inputOperators.size() == 1 && ((inputOperators.contains("\\^") || inputOperators.contains("v"))))) {
                formula = groupByOperator(formula, inputOperators);
                formula = convertToCNF(formula);
            }
        }
        System.out.println("result: " + formula);
        inputOperators = findOperators(formula);
        if (inputOperators.contains("!"))
            inputOperators.remove("!");
        if (inputOperators.size() == 1 && inputOperators.contains("v") && formula.charAt(0) != '(')
            formula = "(" + formula + ")";
        
        String clauseForm = writeInClauseForm(formula);
        System.out.println("Clause form: " + clauseForm);  
    }
    
    /* Populates array lists and preps input for parsing. */
    public static void main(String[] args) {
        populateOperatorList();
        ArrayList<String> input = null;
        String formula = "(A^B)->C";
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
        formula = formula.replaceAll("\\s+",""); // get rid of any whitespace
        processInput(formula);
     
        }
    }
    
