package me.dylanhart.calc.core;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class PostfixTransformer {

    public Queue<Token> toPostfix(Queue<Token> infix) {
        Queue<Token> postfix = new LinkedBlockingQueue<Token>();
        Stack<Token> operatorStack = new Stack<Token>();
        Stack<Token> unaryOperatorStack = new Stack<Token>();

        while (!infix.isEmpty()) {
            Token token = infix.poll();

            switch (token.type) {
            case NUMBER:
            case CONSTANT:
                postfix.offer(token);
                while (!unaryOperatorStack.isEmpty()) {
                    postfix.offer(unaryOperatorStack.pop());
                }
                break;
            case PAREN:
                if (token.value.equals("(")) {
                    operatorStack.push(token);
                } else /* ")" */ {
                    Token op = operatorStack.pop();
                    while (op.type != Token.TokenType.PAREN) {
                        postfix.offer(op);
                        op = operatorStack.pop();
                    }
                    while (!unaryOperatorStack.isEmpty()) {
                        postfix.offer(unaryOperatorStack.pop());
                    }
                }
                break;
            case OPERATOR:
                int priority = getOperatorPriority(token);
                while (!operatorStack.isEmpty() && priority <= getOperatorPriority(operatorStack.peek())) {
                    postfix.offer(operatorStack.pop());
                }
                operatorStack.push(token);
                break;
            case UNARY_OPERATOR:
                unaryOperatorStack.push(token);
                break;
            }
        }

        while (!operatorStack.isEmpty()) {
            postfix.offer(operatorStack.pop());
        }

        return postfix;
    }

    private int getOperatorPriority(Token op) {
        if (op.type != Token.TokenType.OPERATOR) {
            return -1;
        }

        if (op.value.equals("+") || op.value.equals("-")) {
            return 1;
        } else if (op.value.equals("*") || op.value.equals("÷")) {
            return 2;
        } else if (op.value.equals("^")) {
            return 3;
        }

        return 0;
    }
}
