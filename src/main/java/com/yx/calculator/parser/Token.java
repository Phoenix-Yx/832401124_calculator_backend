package com.yx.calculator.parser;

public class Token {
    public enum Type { NUMBER, PLUS, MINUS, STAR, SLASH, LPAREN, RPAREN, EOF }

    public final Type type;
    public final double value;
    public final int pos;

    public Token(Type type, double value, int pos) {
        this.type = type;
        this.value = value;
        this.pos = pos;
    }
}