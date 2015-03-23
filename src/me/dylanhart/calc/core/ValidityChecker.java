package me.dylanhart.calc.core;

import android.util.Log;

import java.util.Queue;
import java.util.TreeSet;

public class ValidityChecker {

    private TreeSet<String> validOperators;
    private String error = null;

    public ValidityChecker() {
        validOperators = new TreeSet<String>();

        validOperators.add("+");
        validOperators.add("-");
        validOperators.add("*");
        validOperators.add("÷");
        validOperators.add("^");
    }

    public boolean isValid(Queue<Token> tokenQueue) {
        Token prev = null;

        int parens = 0;

        for (Token token : tokenQueue) {

            if (token.isKeyword()) {
                Log.d("calc", "mismatched keyword");
                error = "mismatched keyword";
                return false;
            } else if (token.type == Token.TokenType.NUMBER) {
                if (!token.value.matches("^(\\d*\\.)?\\d+$")) {
                    Log.d("calc", "bad number format");
                    error = "bad number format";
                    return false;
                }
            } else if (token.type == Token.TokenType.PAREN) {
                if (token.value.equals("(")) {
                    parens++;
                } else if (token.value.equals(")")) {
                    parens--;
                    if (prev.type == Token.TokenType.PAREN && prev.value.equals("(")) {
                        Log.d("calc", "useless parens");
                        error = "useless parens";
                        return false;
                    } else if (prev.isFunction()) {
                        Log.d("calc", "missing argument for operator");
                        error = "missing argument for operator";
                        return false;
                    }
                } else {
                    Log.d("calc", "paren is not a paren");
                    error = "paren is not a paren";
                    return false;
                }

                if (parens < 0) {
                    Log.d("calc", "unexpected close paren");
                    error = "unexpected close paren";
                    return false;
                }
            } else {
                if (token.isBinaryOperator()) {
                    if (prev == null || (prev.type == Token.TokenType.PAREN && prev.value.equals("(")) || prev.isFunction()) {
                        Log.d("calc", "no left-hand argument for binary operator");
                        error = "no left-hand argument for binary operator";
                        return false;
                    } else if (prev.isBinaryOperator()) {
                        Log.d("calc", "adjacent binary operators");
                        error = "adjacent binary operators";
                        return false;
                    }
                }
            }

            prev = token;
        }

        if (parens != 0) {
            Log.d("calc", "mismatched parens");
            error = "mismatched parens";
            return false;
        } else if (prev != null && prev.isFunction()) {
            Log.d("calc", "hanging operator");
            error = "hanging operator";
            return false;
        }

        error = null;
        return true;
    }

    public String getError() {
        return error;
    }
}
