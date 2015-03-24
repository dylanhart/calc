package me.dylanhart.calc.core;

import android.util.Log;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.Stack;

public class ExpressionEvaluator {

    public String eval(Queue<Token> tokenQueue) {
        if (tokenQueue.isEmpty()) return "0";

        Stack<Double> numberStack = new Stack<Double>();

        while (!tokenQueue.isEmpty()) {
            Token token = tokenQueue.poll();

            switch (token.type) {
            case NUMBER:
                numberStack.push(Double.parseDouble(token.value));
                break;
            case CONSTANT:
                if (token.value.equals("π")) {
                    numberStack.push(Math.PI);
                }
                break;
            case OPERATOR:
                double b = numberStack.pop();
                double a = numberStack.pop();
                numberStack.push(doBinaryOperation(a, b, token.value));
                break;
            case UNARY_OPERATOR:
                numberStack.push(doUnaryOperation(numberStack.pop(), token.value));
            }
        }

        if (numberStack.isEmpty()) {
            return "0";
        } else {
            Double num = numberStack.pop();
            try {
                return BigDecimal.valueOf(num).setScale(10, BigDecimal.ROUND_HALF_UP).toPlainString();
            } catch (NumberFormatException nfe) {
                return num.toString();
            }
        }
    }

    private double doBinaryOperation(double a, double b, String op) {
        if (op.equals("+")) {
            return a + b;
        } else if (op.equals("-")) {
            return a - b;
        } else if (op.equals("*")) {
            return a * b;
        } else if (op.equals("÷")) {
            return a / b;
        } else if (op.equals("^")) {
            return Math.pow(a, b);
        }

        Log.e("calc", "operator not found: " + op);
        return 0;
    }

    private double doUnaryOperation(double a, String op) {
        if (op.equals("sin")) {
            return Math.sin(a);
        } else if (op.equals("cos")) {
            return Math.cos(a);
        } else if (op.equals("tan")) {
            return Math.tan(a);
        } else if (op.equals("asin")) {
            return Math.asin(a);
        } else if (op.equals("acos")) {
            return Math.acos(a);
        } else if (op.equals("atan")) {
            return Math.atan(a);
        } else if (op.equals("sqrt")) {
            return Math.sqrt(a);
        } else if (op.equals("-")) {
            return -a;
        }
        Log.e("calc", "unary operator not found: " + op);
        return 0;
    }

}
