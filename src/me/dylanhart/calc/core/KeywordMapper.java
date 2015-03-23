package me.dylanhart.calc.core;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static me.dylanhart.calc.core.Token.TokenType;

public class KeywordMapper {

    HashMap<String, TokenType> keywordValues;

    public KeywordMapper() {
        keywordValues = new HashMap<String, TokenType>();

        keywordValues.put("sin", TokenType.UNARY_OPERATOR);
        keywordValues.put("cos", TokenType.UNARY_OPERATOR);
        keywordValues.put("tan", TokenType.UNARY_OPERATOR);
        keywordValues.put("asin", TokenType.UNARY_OPERATOR);
        keywordValues.put("acos", TokenType.UNARY_OPERATOR);
        keywordValues.put("atan", TokenType.UNARY_OPERATOR);
        keywordValues.put("sqrt", TokenType.UNARY_OPERATOR);
        keywordValues.put("Ans", TokenType.CONSTANT);
        keywordValues.put("π", TokenType.CONSTANT);
    }

    public Token replaceKeywordToken(Token keywordToken) {
        if (keywordToken.isKeyword()) {
            TokenType type = keywordValues.get(keywordToken.value);
            if (type != null) {
                return new Token(type, keywordToken.value);
            }
        }
        return keywordToken;
    }

    public Queue<Token> mapTokens(Queue<Token> tokenQueue) {
        Queue<Token> mapped = new LinkedBlockingQueue<Token>();

        while (!tokenQueue.isEmpty()) {
            mapped.offer(replaceKeywordToken(tokenQueue.poll()));
        }

        return mapped;
    }

}
