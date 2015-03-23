package me.dylanhart.calc.core;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Tokenizer {

    public Tokenizer() {
    }

    public Queue<Token> read(String txt) {
        StringBuilder currentToken = null;
        Token.TokenType type = null;

        Queue<Token> tokens = new LinkedBlockingQueue<Token>();

        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            Token.TokenType charType = getCharType(c);

            // if we are switching to a new token or if the last token was an operator (1 char long)
            // we add the current token to the queue.
            if (charType != type || type == Token.TokenType.OPERATOR || type == Token.TokenType.PAREN) {
                // if we don't have a current token
                if (type != null) {
                    tokens.offer(new Token(type, currentToken.toString()));
                }

                // create the new token as current
                currentToken = new StringBuilder();
                type = charType;
            }

            // add the character to the current token
            currentToken.append(c);
        }

        // add the last token
        if (currentToken != null)
            tokens.offer(new Token(type, currentToken.toString()));

        return tokens;
    }

    private boolean isNumeric(char c) {
        return c == '.' || (c >= '0' && c <='9');
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private Token.TokenType getCharType(char c) {
        if (isNumeric(c)) return Token.TokenType.NUMBER;
        if (isAlpha(c)) return Token.TokenType.KEYWORD;
        if (c == 'π') return Token.TokenType.KEYWORD;
        if (c == '(' || c == ')') return Token.TokenType.PAREN;
        return Token.TokenType.OPERATOR;
    }
}
