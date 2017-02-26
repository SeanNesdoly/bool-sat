package boolsat;

/*
 * Wrapper class for a propositional logic literal (A or !A).
 *
 * CISC 352 Assignment 2
 * Sean Nesdoly & Mary Hoekstra
 * February 24th, 2017
 */

public class Literal {
    String s; // symbol of literal
    boolean sign; // sign of literal; true=a positive literal, false=a negative literal
    boolean val; // the value actually assigned to the symbol (!A where A has *value* true is evaluated to false)
    boolean isAssigned; // flag for this literal being assigned in the DPLL algorithm

    // parse out a Literal from a String
    public Literal(String litStr) {
        if (litStr != null && litStr.length() == 0)
            System.err.println("Invalid literal instantiation.");
        else if (litStr.charAt(0) == '!') {
            sign = false; // negative literal
            litStr = litStr.substring(1,litStr.length());
        } else
            sign = true; // positive literal

        s = litStr;
        isAssigned = false;
    }

    // creates a literal with the given symbol and sign (sign=false=!A)
    public Literal(String symbol, boolean _sign) {
        this.s = symbol;
        this.sign = _sign;

        isAssigned = false;
    }

    // equivalency is defined on the following attributes: symbol s && sign of literal
    @Override public boolean equals(Object other) {
        if (this == other) return true;

        if (!(other instanceof Literal)) return false;

        Literal otherLit = (Literal)other;

        // TODO: may require comparison of val/isAssigned
        return (s.equals(otherLit.s) && sign == otherLit.sign);
    }

    @Override public int hashCode() {
        int result = 7; // prime number

        // compute hash value
        result = 37*result + s.hashCode();
        result = 37*result + (sign?1:0);

        return result;
    }

    public void setLiteralTrue() {
        this.isAssigned = true;
        
        if (sign)
            val = true; // A is set true to evaluate to true
        else
            val = false; // !A is set false to evaluate to true
    }

    // compute the value of the literal with consideration of its sign
    public boolean computeValue() {
        // literals that have not been assigned default to false
        if (!isAssigned)
            return false;

        if (sign) // positive literal
            return val;
        else // negative literal
            return !val;
    }

    // negate the literal by flipping the boolean value of the sign
    public void negate() {
        sign = !sign;
    }

    // creates an instance of this literal that is negated
    public Literal createNegatedLiteral() {
        return new Literal(this.s, !this.sign);
    }

    // determine if other is the negation of this literal (same symbol, opposite signs)
    public boolean isNegationOf(Literal other) {
        return (this.s.equals(other.s) && this.sign != other.sign);
    }

    // convenience method to print out a literal
    public String toString() {
        return (this.sign ? "" : "!") + s;
    }
}
