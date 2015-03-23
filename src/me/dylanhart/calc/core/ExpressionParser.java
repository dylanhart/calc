package me.dylanhart.calc.core;

import android.util.Log;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ExpressionParser {

    Tokenizer tk;
    KeywordMapper km;
    ValidityChecker vc;
    PostfixTransformer pt;

    public ExpressionParser() {
        tk = new Tokenizer();
        km = new KeywordMapper();
        vc = new ValidityChecker();
        pt = new PostfixTransformer();
    }

    public Queue<Token> read(String txt) {
        Queue<Token> tokenQueue = expandImplicitOperations(km.mapTokens(tk.read(txt)));

        Log.d("calc", "after expand:");
        Log.d("calc", tokenQueue.toString());

        if (vc.isValid(tokenQueue)) {
            return pt.toPostfix(tokenQueue);
        } else {
            return null;
        }
    }

    public Queue<Token> expandImplicitOperations(Queue<Token> tokenQueue) {
        if (tokenQueue.size() < 2) return tokenQueue;
        // <constant or number or )> ( --> <constant or number or )> * (

        Queue<Token> expanded = new LinkedBlockingQueue<Token>();
        int parens = 0;

        Token prev = null;

        while (!tokenQueue.isEmpty()) {
            Token current = tokenQueue.poll();

            if (prev != null && (prev.isValue() || (prev.type == Token.TokenType.PAREN && prev.value.equals(")")))) {
                if (current.isValue() || (current.type == Token.TokenType.PAREN && current.value.equals("("))) {
                    expanded.offer(new Token(Token.TokenType.OPERATOR, "*"));
                }
            }

            if (current.type == Token.TokenType.PAREN) {
                if (current.value.equals("(")) {
                    parens++;
                } else if (current.value.equals(")")) {
                    parens--;
                }
            }

            if (current.isBinaryOperator() && current.value.equals("-")) {
                if (prev == null || prev.isFunction() || prev.type == Token.TokenType.PAREN) {
                    current = new Token(Token.TokenType.UNARY_OPERATOR, current.value);
                }
            }

            expanded.offer(current);

            prev = current;
        }

        Log.d("calc", "parens: " + parens);

        while (parens-- > 0) {
            expanded.offer(new Token(Token.TokenType.PAREN, ")"));
        }

        return expanded;
    }

    public String getError() {
        return vc.getError();
    }

}
