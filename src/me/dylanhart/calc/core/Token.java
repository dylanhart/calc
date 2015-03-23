package me.dylanhart.calc.core;

public class Token {

    public final TokenType type;
    public final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public boolean isValue() {
        return type == TokenType.NUMBER || type == TokenType.CONSTANT;
    }

    public boolean isFunction() {
        return type == TokenType.OPERATOR || type == TokenType.UNARY_OPERATOR;
    }

    public boolean isKeyword() {
        return type == TokenType.KEYWORD;
    }

    public boolean isBinaryOperator() {
        return type == TokenType.OPERATOR;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }

    public static enum TokenType {
        CONSTANT, KEYWORD, NUMBER, OPERATOR, PAREN, UNARY_OPERATOR
    }
}
