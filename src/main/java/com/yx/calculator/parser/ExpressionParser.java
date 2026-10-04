package com.yx.calculator.parser;


public class ExpressionParser {

    private final Tokenizer tokenizer;
    private Token current;

    public ExpressionParser(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new ParseException("表达式不能为空");
        }
        this.tokenizer = new Tokenizer(input);
        this.current = tokenizer.next();
    }

    public static double evaluate(String expression) {
        ExpressionParser parser = new ExpressionParser(expression);
        double result = parser.parseExpression();
        if (parser.current.type != Token.Type.EOF) {
            throw new ParseException("表达式末尾存在多余字符，位置 " + parser.current.pos);
        }
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new ParseException("计算结果非法");
        }
        return result;
    }

    private double parseExpression() {
        double left = parseTerm();
        while (current.type == Token.Type.PLUS || current.type == Token.Type.MINUS) {
            Token.Type op = current.type;
            advance();
            double right = parseTerm();
            left = (op == Token.Type.PLUS) ? left + right : left - right;
        }
        return left;
    }

    private double parseTerm() {
        double left = parseUnary();
        while (current.type == Token.Type.STAR || current.type == Token.Type.SLASH) {
            Token.Type op = current.type;
            advance();
            double right = parseUnary();
            if (op == Token.Type.STAR) {
                left = left * right;
            } else {
                if (right == 0.0) {
                    throw new ParseException("除数不能为零");
                }
                left = left / right;
            }
        }
        return left;
    }

    private double parseUnary() {
        if (current.type == Token.Type.PLUS) {
            advance();
            return parseUnary();
        }
        if (current.type == Token.Type.MINUS) {
            advance();
            return -parseUnary();
        }
        return parsePrimary();
    }

    private double parsePrimary() {
        if (current.type == Token.Type.NUMBER) {
            double v = current.value;
            advance();
            return v;
        }
        if (current.type == Token.Type.LPAREN) {
            advance();
            double v = parseExpression();
            if (current.type != Token.Type.RPAREN) {
                throw new ParseException("缺少右括号 ')' 在位置 " + current.pos);
            }
            advance();
            return v;
        }
        throw new ParseException("非法表达式，位置 " + current.pos);
    }

    private void advance() {
        current = tokenizer.next();
    }
}