# Boolean Satisfiability

>Assignment 2 Technical Document  
>CISC 352: Artificial Intelligence  
>Sean Nesdoly 13sn50 10135490  
>Mary Hoekstra 13meh9 10129863  
>February 13th, 2017  

## 1. Conversion to CNF

When the program is run, the input formula is read in from a text file, stripped of any whitespace, and passed to processInput. This function groups the input expression according to precedence, so it is easier to convert to CNF.

First, *findOperators* is called on the input formula to create a list of all operators used in the particular expression. If this list only contains one operator, and that operator is ‘^’, then there is no need for the expression to be grouped. For example, the expression ‘A^B^C’ is already in CNF and does not need to be grouped by precedence. It can simply be written in clause form as ‘{A,B,C}’. If the only operator is ‘v’, as in ‘AvBvC’, parentheses are wrapped around the whole expression, so that the resulting expression in clause form will be ‘{(A,B,C)}’. If more than one operator is present in the expression, the expression and its operators are passed to *groupByOperator*.

In *groupByOperator*, each operator in the list is iterated through. For each operator, an *otherOperators* list is made. If the operator is ‘^’ or ‘v’, the respective operator is removed from the *otherOperators* list so that during a scan, it is not recognized as another operator. For example, in scanning an expression like ‘A^B^C->D’, the second occurrence of ‘^’ would not be recognized as an operator since it would lead to premature grouping (‘((A^B)^C))->D’, instead of ‘(A^B^C)->D’). Once this list of other operators is established, the operator is matched against the whole expression. For each match, the start and end index of the occurrence of the operator are found. The start and end index are passed to *scanBackwards* and *scanForwards*, respectively.

The functions *scanForwards* and *scanBackwards* scan through the input string and find an appropriate place for a left or right parenthesis. In *scanBackwards*, the input string is stepped through backwards, character by character. The numbers of left and right parentheses are kept track of. If a left parenthesis is seen, and the number of right parentheses matches the number of left parentheses, this is an appropriate place for another left parentheses. If another operator is found, as characterized by the *otherOperators* list, and a right parenthesis has not been seen, this is an appropriate place for a left parenthesis. The loop breaks when the index becomes 0, indicating that the parenthesis should be placed at the front of the expression. Similarly, *scanForwards* steps through the input string and finds an appropriate position for a right parenthesis.

The indices from *scanBackwards* and *scanForwards* are returned to *groupByOperator* and some checks are performed. If the indices are the first and last characters of the input string, respectively, then the entire expression would be bracketed redundantly. Similarly, if there are already parentheses in those indices, a second set of parentheses would be unnecessary. In both of these cases, the operator match is skipped and no parentheses are placed. If the returned index for the left parenthesis is 0, it is simply appended to the front of the string. If the returned index for the right parenthesis is the length of the string, it is simply appended to the end of the string. If none of these special cases apply, the parentheses are inserted into the expression at appropriate positions.

Once the expression is grouped by precedence, it is passed to *convertToCNF*.

In this method, each of the six equivalence rules are checked. Each rule is run on the input while it still matches. This lets expressions like “A<->B” be resolved using the same function, run twice.

Each conversion method is similar in nature. It takes the expression and pulls out the major match groups. For example, in an expression like “(A->B)^(A->C)”, both “A->B” and “A->C” are pulled out as matches. Next, it extracts the subgroups from each match. For “A->B”, the subgroups would be “A” and “B”. It then takes these groups and replaces designated placeholders in an equivalence expression ({1}, {2}, etc.). The bigger group is then replaced by this equivalence and the next group can be worked on. *removeDoubleNegation* is displayed below for reference.

Once converted into CNF, the new formula may contain totally different operators than the original. Once again, the operator list is checked to see if “v” is the only operator, in which case parentheses are wrapped around the whole expression. The expression is then passed to *writeInClauseForm*, where all occurrences of “^” or “v” are replaced by commas, and the expression is wrapped in braces.



## 2. Proof by Refutation

#### DPLL Algorithm Implementation

## 3. Three-Colouring Problem
