package com.yx.calculator.parser;

public class Tokenizer {

    private final String input;
    private int pos = 0;

    public Tokenizer(String input) {
        this.input = input;
    }

    public Token next() {
        skipWhitespace();
        if (pos >= input.length()) {
            return new Token(Token.Type.EOF, 0, pos);
        }
        char c = input.charAt(pos);
        switch (c) {
            case '+': pos++; return new Token(Token.Type.PLUS, 0, pos - 1);
            case '-': pos++; return new Token(Token.Type.MINUS, 0, pos - 1);
            case '*': pos++; return new Token(Token.Type.STAR, 0, pos - 1);
            case '/': pos++; return new Token(Token.Type.SLASH, 0, pos - 1);
            case '(': pos++; return new Token(Token.Type.LPAREN, 0, pos - 1);
            case ')': pos++; return new Token(Token.Type.RPAREN, 0, pos - 1);
            default:
                if (Character.isDigit(c) || c == '.') {
                    return readNumber();
                }
                throw new ParseException("非法字符 '" + c + "' 在位置 " + pos);
        }
    }

    private Token readNumber() {
        int start = pos;
        boolean hasDot = false;
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isDigit(c)) {
                pos++;
            } else if (c == '.') {
                if (hasDot) {
                    throw new ParseException("数字中出现多个小数点，位置 " + pos);
                }
                hasDot = true;
                pos++;
            } else {
                break;
            }
        }
        String numStr = input.substring(start, pos);
        if (numStr.equals(".")) {
            throw new ParseException("非法数字 '.' 在位置 " + start);
        }
        try {
            return new Token(Token.Type.NUMBER, Double.parseDouble(numStr), start);
        } catch (NumberFormatException e) {
            throw new ParseException("非法数字 '" + numStr + "'");
        }
    }

    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }
}